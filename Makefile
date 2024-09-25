JAVAC = javac
JAVA = java

SRC_DIR = src/jacom
TOOL_DIR = tool
TEST_DIR = test

GEN_AST = $(TOOL_DIR)/GenerateAst.java
LOX = $(SRC_DIR)/Lox.java

all: jacom

jacom:
	$(JAVAC) $(LOX)

ast:
	$(JAVAC) $(GEN_AST)
	$(JAVA) tool.GenerateAst $(SRC_DIR)

run: jacom
	$(JAVA) src.jacom.Lox $(ARGS)

test: jacom
	$(JAVA) src.jacom.Lox $(TEST_DIR)/test.lox

clean:
	rm -rf $(SRC_DIR)/*.class $(TOOL_DIR)/*.class

.PHONY: jacom ast run clean test