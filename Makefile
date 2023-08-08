all: compile

compile: Calculator.java CalculatorImplementation.java CalculatorServer.java CalculatorClient.java CalculatorTests.java
	javac -d classfiles $^

server: compile
	java -classpath classfiles assignment1.CalculatorServer &

test: compile
	java -classpath classfiles assignment1.CalculatorTests