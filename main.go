package main

import (
	"flag"
	"fmt"
	"github.com/aellwein/nubilo/nubilo"
	"os"
)

var (
	logger = nubilo.NewLogger("nubilo", nubilo.InfoLevel)
)

func checkError(err error) {
	if err != nil {
		fmt.Printf("error occurred: %s\n", err)
		os.Exit(1)
	}
}

func main() {
	var pathToApp string
	var debug bool

	flag.StringVar(&pathToApp, "webapp", "", "provide path to webapp")
	flag.BoolVar(&debug, "debug", false, "enable debug mode")
	flag.Parse()
	if debug {
		logger.SetLevel(nubilo.DebugLevel)
	}

	logger.Debug("Debug logging is on.")
	ctx := nubilo.Init(pathToApp)
	err := ctx.Run(":4201")
	checkError(err)
}
