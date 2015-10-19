BUSTED ?= busted

.PHONY: doc

all: test

test:
	@$(BUSTED)

coverage:
	@$(BUSTED) -c

doc:
	$(MAKE) -C doc html

clean:
	$(RM) lcov.*.out luacov.*.out

install:
	@echo "Sorry, installation is not implemented yet :("
	@exit 1
