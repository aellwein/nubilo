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

-------------------------------------------------------------------------------
-- Makes given table immutable, i.e. either no new members can be added
-- to the table nor the existing ones can be changed.
-- @param #table t to make immutable.
-------------------------------------------------------------------------------
foundation.immutable = function(self, t)
  assert(type(t) == 'table', "must be of type table")
  local proxy = {}
  local mt = {
    __index = t,
    __tostring = t.__tostring,
    __newindex = function(t,k,v)
      error("attempt to modify an immutable object", 2)
    end,
    __metatable = false
  }
  setmetatable(proxy, mt)
  return proxy
end

-------------------------------------------------------------------------------
-- foundation.Object is the root of the foundation's object hierarchy.
-------------------------------------------------------------------------------
foundation.Object = {}
foundation.Object.name = "foundation.Object"
foundation.Object.super = function() return nil end -- no parent here
foundation.Object.Object = function() end --default constructor, noop
foundation.Object._mixins = {}
foundation.Object.__tostring = function() return "foundation.Object()" end
foundation.Object = foundation:immutable(foundation.Object)

-------------------------------------------------------------------------------
-- checks if argument is a foundation class or its instance.
-- @param #table t argument to check.
-------------------------------------------------------------------------------
foundation.isClass = function(self, t)
  if type(t) ~= "table" then return false end
  if not t.super or type(t.super) ~= "function" then return false end

  return true
end

-------------------------------------------------------------------------------
-- This function prepares a foundation class blueprint. This means, functions
-- and attributes can be added to the table, but it is not ready to be used
-- as instance yet.
-- @param #string name name of the foundation class, should by convention begin 
-- with an uppercase letter.
-- @param #class base an instance of foundation class which is to be the parent
-- for this class.
------------------------------------------------------------------------------- 
foundation.class = function(self, name, base)
  assert(name,"name may not be nil")
  assert(string.len(name)>0, "name may not be empty")
  assert(string.sub(name,1,1) == string.upper(string.sub(name,1,1)), 
        "name should have its first letter in upper case by convention")
  local clazz = {}
  if base then
    assert(self:isClass(base), "base should be a foundation class")
    clazz.super = function() return base end
  else
    clazz.super = function() return self.Object end
  end
  return clazz
end

return foundation