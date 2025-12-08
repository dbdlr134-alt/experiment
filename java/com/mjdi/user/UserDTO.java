package com.mjdi.user;

public class UserDTO {
    private String jdi_user;
    private String jdi_pass;
    private String jdi_name;
    private String jdi_email;
    private String jdi_phone;
    private String jdi_profile;
    private String jdi_role;
    private String jdi_status;
    
    // ★ [필수] 테마 필드 추가
    private String jdi_theme;

    public UserDTO() {}

    // Getter & Setter
    public String getJdi_user() { return jdi_user; }
    public void setJdi_user(String jdi_user) { this.jdi_user = jdi_user; }
    public String getJdi_pass() { return jdi_pass; }
    public void setJdi_pass(String jdi_pass) { this.jdi_pass = jdi_pass; }
    public String getJdi_name() { return jdi_name; }
    public void setJdi_name(String jdi_name) { this.jdi_name = jdi_name; }
    public String getJdi_email() { return jdi_email; }
    public void setJdi_email(String jdi_email) { this.jdi_email = jdi_email; }
    public String getJdi_phone() { return jdi_phone; }
    public void setJdi_phone(String jdi_phone) { this.jdi_phone = jdi_phone; }
    public String getJdi_profile() { return jdi_profile; }
    public void setJdi_profile(String jdi_profile) { this.jdi_profile = jdi_profile; }
    public String getJdi_role() { return jdi_role; }
    public void setJdi_role(String jdi_role) { this.jdi_role = jdi_role; }
    public String getJdi_status() { return jdi_status; }
    public void setJdi_status(String jdi_status) { this.jdi_status = jdi_status; }
    
    // ★ [추가] 테마 Getter/Setter
    public String getJdi_theme() { return jdi_theme; }
    public void setJdi_theme(String jdi_theme) { this.jdi_theme = jdi_theme; }
    
    public UserDTO(String jdi_user, String jdi_pass, String jdi_name, String jdi_email, String jdi_phone) {
        this.jdi_user = jdi_user;
        this.jdi_pass = jdi_pass;
        this.jdi_name = jdi_name;
        this.jdi_email = jdi_email;
        this.jdi_phone = jdi_phone;
    }
}