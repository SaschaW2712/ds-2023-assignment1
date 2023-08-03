To use this project, follow these steps:
1. Compile the project ("make" or "make compile")
2. Navigate to the classfiles directory ("cd classfiles") and start the registry ("rmiregistry &")
3. Return to the root directory ("cd ..")
4. Start the server ("make server")
5. Run a client or automated tests:\
(a) To run automated tests, run "make test".\
(b) To run a client using the calculator server, see "Run client with custom input" below.

<br >

Run client with custom input:\
java  -classpath classfiles assignment1.CalculatorClient [CLI arguments]\
- E.g. java  -classpath classfiles assignment1.CalculatorClient 3 4 5 6 min pop
- Ensure server is running first

<br >

CLIENT INPUTS:\
When passing input to a client, commands are given as space-separated command-line arguments.\
The first argument must be the output file name, or 'sysout' for standard System output.\
Any number of arguments can follow, which will be executed on that client's stack on the server.\
Valid operations are:
- An integer value: Will push the integer onto the client's stack
- 'min', 'max', 'lcm', or 'gcd': Will pop all stack elements and push the operation's result
- 'pop': Will pop the top element from the stack and print it
- 'empty': Will print 'true' or 'false' depending on whether the stack is empty
- 'delaypop': Must be followed by an integer argument, and will pop with the following argument's delay in milliseconds.
  - E.g. "delaypop 2000" will run pop with delay 2000ms


MAKEFILE TARGETS:\

Compile classfiles without running anything:\
make

<br >

Start server:\
make server
- This will also recompile the class files if they've changed

<br >

Run tests:\
make test
- This will also recompile the class files if they've changed

<br >