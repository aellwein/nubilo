
Design Decisions
================


Performance of code execution
-----------------------------

In order to provide a decent performance of code execution, we decided to implement Nubilo
in `Lua <http://lua.org>`_ language. As Lua interpreter we want to use `LuaJIT <http://www.luajit.org>`_,
for just-in-time bytecode compilation and better performance. 