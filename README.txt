* If there are more than one credentials with the same username, only the last credential will be effective.

* If a client terminates session without logging out or being logged out by server (for example, terminates by ctrl+c), attempt to log in again with the same account will result in ConnectException on server side.