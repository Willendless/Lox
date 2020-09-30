SOURCES=$(wildcard src/**/*.java src/*.java)
OBJECTS=$(patsubst %.java,%.class,$(SOURCES))

TARGET=jlox/Lox

all: build $(TARGET)

$(TARGET): $(SOURCES)
	javac -d build/ $^

build:
	@mkdir -p build

# usage: make run ifile='intpufile.txt'
run:
	java -classpath build/ $(TARGET) $(ifile)

clean:
	rm -rf build/*
	rmdir build
