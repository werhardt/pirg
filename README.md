# pirg
[![Build Status](https://travis-ci.com/werhardt/pirg.svg?branch=master)](https://travis-ci.com/werhardt/pirg)

Yet another http client.

pirg is a helper library to send data to a configured server.


##### Configuration

The configuration and it's default values.
```
# Start sending of messages on creation of the Sender object (default = true)
# If false, sending has to be started manually with the Sender.start() method.
pirg.autostart=true

# The destination URL of the data to send -> configuration is mandatory
pirg.url=http://localhost:8086/pirg

# The interval when the data should be send in seconds (default = 10)
pirg.sendinterval=1

# The number of messages to send on each interval (default = 100)
prig.sendsize=100
```

##### Usage
```java
String url = "..."; // the destination url
String json = "..."; // the data to send

Message msg = Message.build().url(url).body(json);

Sender sender = new Sender();
sender.send(msg);
```