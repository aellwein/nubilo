Feature: Self-setup on first start

  Scenario: Database schema creation
    Given database file is not present
    When nubilo is started
    Then database file is created with default settings
    And entry page is redirected for the first login

  Scenario: No redirect if database file already exists
    Given database file is already present
    When nubilo is started
    Then login page is displayed