# design-doc4: evaluating expressions

1. 值的表示： java的object对象
    + 利用instanceof确定运行时类型

## 运行时错误处理

+ 运行时，类型转换前进行check若失败则抛出自定义RuntimeError
+ interpret()方法中捕获
+ Lox类中实现error reporting
