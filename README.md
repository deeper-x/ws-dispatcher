# Resultset dispatcher

Build:
```bash
$ gradle -t build
```

Run:
```bash
$ gradle run
```

Install a ws client:
```bash
$ npm install -g wscat
```
Http is a stateless request/response protocol.
WebSockets are a subprotocol of http, and start as a normal http-request (upgrade request header), then connection switches to be a bidirectional communication .
The smallest unit of transmission that can be sent as part of the WebSocket protocol, a Frame, defines:
- a type
- a length
- a payload

Client conntected to the endpoint, sends Payload (binary or text). To consume services, launch client on requesting host and start communications:

```bash
# HOST 1
wscat -c 'ws://<server>:8080/ws-server'
> SELECT foo, bar, baz FROM tableX;
> <[getting resultSet... ]>

# HOST 2
wscat -c 'ws://<server>:8080/ws-server'
> <[getting resultSet... ]>

# HOST  2
wscat -c 'ws://<server>:8080/ws-server'
> <[getting resultSet... ]>
```









