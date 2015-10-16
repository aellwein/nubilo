--[[
Copyright 2015, Alexander Ellwein, Jürgen Fickel

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
]]--

local foundation = {}

foundation.isClass = function(self, t) 
  if type(t) ~= "table" then return false end
  if not t.super or type(super) ~= "function" then return false end
  return true
end

foundation.instanceof = function(self, t)
  assert(self:isClass(t), "is not a foundation class")
end

-- makes a table immutable
foundation.immutable = function(self, t) 
  assert(type(t) == 'table', "must be of type table")
  local proxy = {}
  local mt = {
    __index = t,
    __newindex = function(t,k,v)
        error("attempt to modify an immutable object", 2)
    end
  }
  setmetatable(proxy, mt)
  return proxy
end

-- class() prepares a class blueprint
foundation.class = function(self, name, base)
  assert(name,"name may not be nil")
  assert(string.len(name)>0, "name may not be empty")
  assert(string.sub(name,1,1) == string.upper(string.sub(name,1,1)), "name should have its first letter in upper case")
  if base then
    assert(self:isClass(base), "base should be a foundation class")
  end
end

-- new() creates a class instance, i.e. calls a constructor
foundation.new = function(self, ...)
end  


return foundation