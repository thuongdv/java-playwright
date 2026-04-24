package com.example.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object for the application home page.
 *
 * <p>Encapsulates all selectors and interactions for this page so tests
 * remain free of low-level Playwright calls.
 */
public class HomePage {

    private final Page page;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final Locator searchBox;
    private final Locator searchButton;
    private final Locator pageHeading;

    public HomePage(Page page) {
        this.page         = page;
        this.searchBox    = page.locator("[name='q']");
        this.searchButton = page.locator("[name='btnK']").first();
        this.pageHeading  = page.locator("h1").first();
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void navigate(String baseUrl) {
        page.navigate(baseUrl);
    }

    // ── Interactions ──────────────────────────────────────────────────────────

    public void search(String query) {
        searchBox.fill(query);
        searchButton.click();
    }

    public void typeInSearchBox(String text) {
        searchBox.fill(text);
    }

    // ── Assertions helpers ────────────────────────────────────────────────────

    public String getTitle() {
        return page.title();
    }

    public String getHeadingText() {
        return pageHeading.innerText();
    }

    public boolean isSearchBoxVisible() {
        return searchBox.isVisible();
    }

    public String getCurrentUrl() {
        return page.url();
    }
}
