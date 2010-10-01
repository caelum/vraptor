Scenario: User handles dvds

Given user is unlogged
When user tries to login as "guilherme silveira"
Then an error is displayed
Then run is successful

Given user is unlogged
When user tries to login as "johnny"
Then the user "johnny" is logged in
Then run is successful
