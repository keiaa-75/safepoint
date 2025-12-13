package com.keiaa.safepoint.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "navigation")
public class NavigationConfig {
    
    private List<NavItem> admin;
    private List<NavItem> student;
    
    public List<NavItem> getAdmin() {
        return admin;
    }
    
    public void setAdmin(List<NavItem> admin) {
        this.admin = admin;
    }
    
    public List<NavItem> getStudent() {
        return student;
    }
    
    public void setStudent(List<NavItem> student) {
        this.student = student;
    }
    
    public List<NavItem> getAdminNav() {
        return admin;
    }
    
    public List<NavItem> getStudentNav() {
        return student;
    }
    
    public static class NavItem {
        private String name;
        private String route;
        private String icon;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getRoute() { return route; }
        public void setRoute(String route) { this.route = route; }
        
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
    }
}
