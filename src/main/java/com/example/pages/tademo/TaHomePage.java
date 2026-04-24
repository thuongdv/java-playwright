package com.example.pages.tademo;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.GetByRoleOptions;
import com.microsoft.playwright.options.AriaRole;

public class TaHomePage {
  private final Locator loginSignUpButton;

  public TaHomePage(Page page) {
    this.loginSignUpButton =
        page.getByRole(AriaRole.LINK, new GetByRoleOptions().setName("Log in / Sign up"));
  }

  public void clickLoginSignUp() {
    this.loginSignUpButton.click();
  }
}
