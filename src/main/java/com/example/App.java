package com.example;

import java.util.logging.Logger;

/** Hello world! */
public class App {
  private static final Logger logger = Logger.getLogger(App.class.getName());

  public static void main(String[] args) {
    System.out.println("Hello World!");
    String name = "Playwright";
    logger.info("Hello " + name + "!");
    logger.info("Hello World!");
  }
}
