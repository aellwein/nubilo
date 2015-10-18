.PHONY: doc

all: test

test:
	@busted

doc:
	$(MAKE) -C doc html

install:
	@echo "Sorry, installation is not implemented yet :("
	@exit 1
