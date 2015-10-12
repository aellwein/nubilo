
Concepts
========

Reach the server behind firewall/proxy
--------------------------------------

In order to be reached from behind a firewall or a proxy, Nubilo server should be able to
transfer all the data via port 80 (HTTP) or 443 (HTTPS). For a binary or custom data transfers,
a WebSocket endpoint can be opened on the same server port under a different context path.
As a workaround, a reverse proxy can always be used to pass the traffic inside HTTP, but we
want to give that possibility already out-of-the-box.

**Implications**:

* Server has to be started as root, but after establishing a listening socket connection on a 
  privileged socket, it has to drop the root's privileges.
* Configuration of the server must include a UID and GID to run under.


