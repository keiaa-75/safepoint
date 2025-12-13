package com.keiaa.safepoint.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "navigation")
public class NavigationConfig {
    
    private Map<String, List<NavItem>> navigation;
    
    public Map<String, List<NavItem>> getNavigation() {
        return navigation;
    }
    
    public void setNavigation(Map<String, List<NavItem>> navigation) {
        this.navigation = navigation;
    }
    
    public List<NavItem> getStudentNav() {
        return navigation.get("student");
    }
    
    public List<NavItem> getAdminNav() {
        return navigation.get("admin");
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
