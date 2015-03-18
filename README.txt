* If a client messages himself, the message goes through.

* If the username a client requests to block is already blocked, the requests changes nothing on the server side and returns success. Same thing for unblocking a username that was not blocked.

* When client A is blocked by client B, A messaging B is considered to be an error, while A broadcasting is not.

* If there are more than one credentials with the same username, only the last credential will be effective.

* If a client terminates session without logging out or being logged out by server (for example, terminates by ctrl+c), attempt to log in again with the same account before TIME_OUT will result in ConnectException on server side.