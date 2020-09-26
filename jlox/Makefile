SOURCES=$(wildcard src/**/*.java src/*.java)
OBJECTS=$(patsubst %.java,%.class,$(SOURCES))

TARGET=jlox/Lox

all: build $(TARGET)

$(TARGET): $(SOURCES)
	javac -d build/ $^

build:
	@mkdir -p build

run: $(TARGET)
	java -classpath build/ $(TARGET)

clean:
	rm -rf build/*
	rmdir build
