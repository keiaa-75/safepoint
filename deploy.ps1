/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("start", "stop")]
    [string]$Action
)

$PORT = 9090
$APP_URL = "http://localhost:$PORT"

$SpringBootProcess = $null
$CloudflaredJob = $null

$Global:TunnelUrl = $null

function Write-Log {
    param([string]$Message)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] $Message"
}

function Check-Prerequisites {
    if (!(Get-Command java -ErrorAction SilentlyContinue)) {
        Write-Log "ERROR: java is not in PATH. Please install Java and ensure it's in your PATH."
        exit 1
    }

    if (!(Get-Command cloudflared -ErrorAction SilentlyContinue)) {
        Write-Log "ERROR: cloudflared is not in PATH. Please install cloudflared and ensure it's in your PATH."
        exit 1
    }

    if ([string]::IsNullOrEmpty($env:GITHUB_TOKEN)) {
        Write-Log "ERROR: GITHUB_TOKEN environment variable is not set."
        exit 1
    }
}

function Start-Application {
    Write-Log "Building Spring Boot application..."
    $buildResult = Start-Process -FilePath "mvn" -ArgumentList "clean", "package", "-DskipTests" -PassThru -WorkingDirectory "$PSScriptRoot" -NoNewWindow
    $buildResult.WaitForExit()
    
    if ($buildResult.ExitCode -ne 0) {
        Write-Log "ERROR: Failed to build the application."
        exit 1
    }

    $jarFiles = Get-ChildItem -Path "$PSScriptRoot/target/" -Filter "*.jar" | Where-Object { $_.Name -notlike "*-sources.jar" }
    if ($jarFiles.Count -eq 0) {
        Write-Log "ERROR: No JAR file found in target directory."
        exit 1
    }
    
    $jarPath = $jarFiles[0].FullName
    Write-Log "Starting Spring Boot application from JAR: $jarPath"
    
    $SpringBootProcess = Start-Process -FilePath "java" -ArgumentList "-jar", "`"$jarPath`"", "--server.port=$PORT" -PassThru -WorkingDirectory "$PSScriptRoot" -NoNewWindow
    
    $maxRetries = 30
    $retryCount = 0
    $serverReady = $false
    
    do {
        Start-Sleep -Seconds 2
        $retryCount++
        
        try {
            $response = Invoke-RestMethod -Uri $APP_URL -Method Get -TimeoutSec 5
            $serverReady = $true
            Write-Log "Application is running at $APP_URL"
        }
        catch {
            if ($retryCount -lt $maxRetries) {
                Write-Log "Waiting for application to start... (attempt $retryCount/$maxRetries)"
            }
            else {
                Write-Log "ERROR: Failed to reach $APP_URL after $maxRetries attempts. Killing processes and exiting."
                Stop-Processes
                exit 1
            }
        }
    } while (-not $serverReady)
}

function Start-Tunnel {
    Write-Log "Starting Cloudflare tunnel..."

    $job = Start-Job -ScriptBlock {
        param($url)
        cloudflared tunnel --url $url 2>&1
    } -ArgumentList $APP_URL
    
    $timeout = 60
    $elapsed = 0
    $checkInterval = 5
    $foundUrl = $null
    
    while ($elapsed -lt $timeout) {
        Wait-Job -Job $job -Timeout $checkInterval 2>$null
        Start-Sleep -Seconds $checkInterval
        $elapsed += $checkInterval

        $output = Receive-Job -Job $job

        foreach ($line in $output) {
            $regex = "https://[a-z0-9-]+\.trycloudflare\.com"
            if ($line -match $regex) {
                $foundUrl = $matches[0]
                $Global:TunnelUrl = $foundUrl
                break
            }
        }
        
        if ($foundUrl) {
            Write-Log "Found tunnel URL: $foundUrl"

            $Global:CloudflaredJob = $job
            return $foundUrl
        }
    }
    
    Write-Log "ERROR: Could not find tunnel URL after $timeout seconds."

    Stop-Job -Job $job
    Remove-Job -Job $job
    
    return $null
}

function Update-DocsIndex {
    param([string]$TunnelUrl)
    
    $docsPath = Join-Path $PSScriptRoot "docs" "index.html"
    
    if (Test-Path $docsPath) {
        $content = Get-Content $docsPath -Raw
        $updatedContent = $content -replace "https://[a-z0-9-]+\.trycloudflare\.com", $TunnelUrl

        Set-Content -Path $docsPath -Value $updatedContent
        
        Write-Log "Updated $docsPath with new tunnel URL: $TunnelUrl"
    }
    else {
        Write-Log "ERROR: $docsPath does not exist."
        return $false
    }
    
    return $true
}

function Commit-And-Push {
    Write-Log "Committing and pushing changes..."
    git add "docs/index.html"
    if ($LASTEXITCODE -ne 0) {
        Write-Log "ERROR: Failed to add docs/index.html to git."
        return $false
    }

    $commitResult = git commit -m "Update tunnel URL in docs"
    if ($LASTEXITCODE -ne 0) {
        Write-Log "ERROR: Failed to commit changes."
        return $false
    }

    $pushResult = git push origin HEAD
    if ($LASTEXITCODE -ne 0) {
        Write-Log "ERROR: Failed to push changes to GitHub."
        return $false
    }
    
    Write-Log "Successfully committed and pushed changes."
    return $true
}

function Stop-Processes {
    Write-Log "Stopping processes..."
    
    if ($SpringBootProcess -and !$SpringBootProcess.HasExited) {
        try {
            Stop-Process -Id $SpringBootProcess.Id -Force:$false
            Write-Log "Sent termination signal to Spring Boot process (PID: $($SpringBootProcess.Id))"

            Start-Sleep -Seconds 3

            if (!$SpringBootProcess.HasExited) {
                Stop-Process -Id $SpringBootProcess.Id -Force
                Write-Log "Force stopped Spring Boot process (PID: $($SpringBootProcess.Id))"
            }
        }
        catch {
            Stop-Process -Id $SpringBootProcess.Id -Force
            Write-Log "Force stopped Spring Boot process (PID: $($SpringBootProcess.Id))"
        }
    }
    
    if ($Global:CloudflaredJob -and (Get-Job -Id $Global:CloudflaredJob.Id -ErrorAction SilentlyContinue)) {
        try {
            Stop-Job -Job $Global:CloudflaredJob
            Remove-Job -Job $Global:CloudflaredJob
            Write-Log "Stopped Cloudflared job (ID: $($Global:CloudflaredJob.Id))"
        }
        catch {
            Remove-Job -Job $Global:CloudflaredJob -Force
            Write-Log "Force removed Cloudflared job (ID: $($Global:CloudflaredJob.Id))"
        }
    }
}

function Do-Start {
    Check-Prerequisites
    
    Start-Application
    
    $tunnelUrl = Start-Tunnel
    if ($null -eq $tunnelUrl) {
        Write-Log "ERROR: Failed to get tunnel URL. Killing processes and exiting."
        Stop-Processes
        exit 1
    }
    
    $updateSuccess = Update-DocsIndex -TunnelUrl $tunnelUrl
    if (!$updateSuccess) {
        Write-Log "ERROR: Failed to update docs/index.html. Killing processes and exiting."
        Stop-Processes
        exit 1
    }
    
    $commitSuccess = Commit-And-Push
    if (!$commitSuccess) {
        Write-Log "ERROR: Failed to commit and push changes. Killing processes and exiting."
        Stop-Processes
        exit 1
    }
    
    Write-Log "Application deployed successfully with tunnel URL: $tunnelUrl"
    Write-Log "The application and tunnel are running in the background. Use 'deploy.ps1 stop' to stop them."
}

function Do-Stop {
    Write-Log "Stopping application and tunnel..."
    Stop-Processes
    Write-Log "Application and tunnel stopped."
    exit 0
}

switch ($Action) {
    "start" { 
        Do-Start
        break 
    }
    "stop" { 
        Do-Stop
        break 
    }
}
