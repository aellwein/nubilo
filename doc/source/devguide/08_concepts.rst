
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


Coroutines vs. threads
----------------------

By using Lua we have native support for so-called *coroutines*. The coroutines allow using multitasking
(i.e. context switching between different flows of execution) without invoking preemptive threads. 

But what does it mean?

In common asynchronous programming, the pattern used with threaded programming is mostly the same:

* you fork a thread which either
    * uses a snapshot (its own copy) of a shared data to process or
    * uses locks to access the data concurrently with other threads;
* After processing results, you either have 
    * to "join" (block, wait, synchronize) means waiting until the thread has 
      completed its processing or
    * to proceed as no waiting is required in the case that the thread 
      has just ended ("fire-and-forget" threads).

Both approaches have their drawbacks. The first one costs (temporary) memory to create data snapshot plus 
the costs of synchronization (waiting for thread, synchronizing the data etc.). The second approach 
("fire-and-forget" thread) costs CPU cycles for locking on every access to shared resources. 

Using coroutines means that you agree to give up execution of your current flow to another coroutine. 
This is called *yielding*. It means that a context switch is under your control: your 
flow of execution decides, when to work and when to yield to another coroutine. 
As there can be only one coroutine active at a time, no locks whatsoever are 
needed to access shared data, means no time is lost by synchronization of any kind. 
Because the coroutines are also cheaper as threads (only stack frames being saved), 
a program can have many of them running without consuming much memory. This is 
perfect especially for systems running on weaker hardware.

There are some disadvantages on using coroutines. Because of their *cooperative* nature, coroutines 
may not block for a longer period of time, as they would "steal" CPU time
of yielded coroutines. This means, you cannot do tasks, which may consume
undeterminable amounts of time, in a blocking fashion (these are mostly the
tasks, like network clients, communication patterns, interacting with external
systems and so on). For this kind of tasks it is advisable to yield often, so
that other pending coroutines will have a chance to resume their execution.
Indeed, the whole programming model of an application have to be changed. The
another disadvantage of coroutines is, that they are not scheduled by OS across
the CPUs, as there is no preemption and only one execution flow is running.
The later may be a disadvantage in a low-latency applications: for example,
scientific calculation applications require all the CPU they can get, so
preemptive multi-threaded paradigm may work better there. For usual
applications, like client/server systems, it is not the CPU, it is the I/O
(and RAM) which is crucial for scaling. In this case, the coroutines are a
better choice.

**Implications**:

* The whole application has to be lay out to respect the cooperative 
  multitasking: concurrent executions have to be designed in the way they 
  can *yield* as soon as possible, without exceptions. This is a major 
  concept change from using preemtive multitasking.
* No multi CPU scheduling can be used.
* No impicit locking on shared resources is needed.


