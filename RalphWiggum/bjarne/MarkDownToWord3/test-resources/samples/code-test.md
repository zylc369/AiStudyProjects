# Code Blocks Test

This file tests code blocks and inline code.

## Inline Code

Here is some `inline code` in the middle of a sentence.

You can also use `inline code` at the start or end of sentences.

## Fenced Code Blocks

### Java Code

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

### Python Code

```python
def greet(name):
    return f"Hello, {name}!"

print(greet("World"))
```

### JavaScript Code

```javascript
function greet(name) {
    return `Hello, ${name}!`;
}

console.log(greet("World"));
```

### Shell Script

```bash
#!/bin/bash
echo "Hello, World!"
```

### JSON

```json
{
  "name": "John Doe",
  "age": 30,
  "city": "New York"
}
```

### Code Block with Empty Lines

```java
public class Example {


    public void method() {


        System.out.println("Indented code");


    }

}
```

### Code Block with Special Characters

```regex
^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
```

### Multiple Inline Code in One Line

Use `var`, `let`, or `const` to declare variables in JavaScript.

### Code Block with Backticks

```
This is a code block without a language identifier.
It can contain backticks `like this` without issues.
```

**End of Code Test**
