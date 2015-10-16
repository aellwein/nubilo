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

local foundation = require "modules.core.foundation"
local assert = require("luassert")

describe("foundation module", 
  function()
    
    -- isClass()
    describe("isClass() function",
    function()
      it("returns false if argument is not a table",
      function()
        assert.falsy(foundation:isClass(""))
      end)
  
      it("returns false if argument is not a foundation class",
      function()
        assert.falsy(foundation:isClass({}))
      end)
    end)
    
    -- class()
    describe("class() function", 
    function()
      
      it("disallows nil class name", 
      function()
        assert.has.errors(function() foundation:class(nil) end, "name may not be nil")
      end)
    
      it("disallows empty class name",
      function()
        assert.has.errors(function() foundation:class("") end, "name may not be empty")
      end)
    
      it("disallows lowercased class names *by convention*",
      function()
        assert.has.errors(function() foundation:class("myClass") end, "name should have its first letter in upper case")
      end)
    
      it("disallows base arg of non class type",
      function()
        assert.has.errors(function() foundation:class("MyClass", {}) end, "base should be a foundation class")
      end)
    
    end)
  
  end)