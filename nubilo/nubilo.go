package nubilo

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"os"
	"path"
)

type Ctx struct {
	webappPath string
	router     *gin.Engine
}

func checkError(err error) {
	if err != nil {
		fmt.Printf("error occurred: %s\n", err)
		os.Exit(1)
	}
}

func Init(webappPath string) *Ctx {
	ctx := &Ctx{}
	ctx.webappPath = webappPath
	return ctx
}

func (ctx *Ctx) Run(addr string) error {
	ctx.router = gin.Default()
	fmt.Printf("Hier! %s\n", addr)
	ctx.router.Static("/", path.Join(ctx.webappPath, "dist"))
	err := ctx.router.Run(addr)
	return err
}
