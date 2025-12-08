package com.mjdi.user;

public class ThemeDTO {
    private String themeCode;   // 테마 코드 (폴더명)
    private String themeName;   // 테마 이름
    private int price;          // 가격
    private String description; // 설명
    private String isActive;    // 판매 여부

    public ThemeDTO() {}

    public ThemeDTO(String themeCode, String themeName, int price, String description) {
        this.themeCode = themeCode;
        this.themeName = themeName;
        this.price = price;
        this.description = description;
    }

    public String getThemeCode() { return themeCode; }
    public void setThemeCode(String themeCode) { this.themeCode = themeCode; }
    public String getThemeName() { return themeName; }
    public void setThemeName(String themeName) { this.themeName = themeName; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
}