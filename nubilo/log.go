package nubilo

import (
	"log"
	"os"
)

type LogLevel int

const (
	TraceLevel   LogLevel = 0
	DebugLevel   LogLevel = 1
	InfoLevel    LogLevel = 2
	WarningLevel LogLevel = 3
	ErrorLevel   LogLevel = 4
	PanicLevel   LogLevel = 5
)

type Logger struct {
	name  string
	log   *log.Logger
	level LogLevel
}

func NewLogger(name string, level LogLevel) *Logger {
	logger := &Logger{name: name}
	logger.log = log.New(os.Stderr, "", log.LstdFlags)
	logger.level = level
	return logger
}

func (logger *Logger) SetLevel(level LogLevel) {
	logger.level = level
}

func (logger *Logger) _log(level string, content interface{}) {
	logger.log.Printf("%s | %s | %s\n", logger.name, level, content)
}

func (logger *Logger) Trace(l interface{}) {
	if logger.level == TraceLevel {
		logger._log("TRACE", l)
	}
}

func (logger *Logger) Debug(l interface{}) {
	if logger.level <= DebugLevel {
		logger._log("DEBUG", l)
	}
}

func (logger *Logger) Info(l interface{}) {
	if logger.level <= InfoLevel {
		logger._log("INFO", l)
	}
}

func (logger *Logger) Warning(l interface{}) {
	if logger.level <= WarningLevel {
		logger._log("WARNING", l)
	}
}

func (logger *Logger) Error(l interface{}) {
	if logger.level <= ErrorLevel {
		logger._log("ERROR", l)
	}
}

func (logger *Logger) Panic(l interface{}) {
	if logger.level == PanicLevel {
		logger.log.Panicf("%s | PANIC | %s\n", logger.name, l)
	}
}
