SOURCES=$(wildcard src/**/*.java src/*.java)
OBJECTS=$(patsubst %.java,%.class,$(SOURCES))

TARGET=jlox/Lox
AST=tool/GenerateAST
GEN=Expr.java
outputdir=./src/jlox

all: build $(TARGET) generate_ast 

$(TARGET): $(SOURCES)
	javac -d build/ $^

build:
	@mkdir -p build

# usage: make run ifile='intpufile.txt'
run: $(TARGET)
	java -classpath build/ $(TARGET) $(ifile)

# usage: make generate_ast outputdir='./src/jlox'
generate_ast:
	java -classpath build/ $(AST) $(outputdir)

rm_generation:
	rm src/jlox/$(GEN)

clean:
	rm -rf build/*
	rmdir build
