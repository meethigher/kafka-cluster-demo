注意事项

1. 如果Kafka不存在对应的topic或者分区，会WARN提示`Error while fetching metadata with correlation id 2`，一般会自动创建，下次就不会提示了。如果并未自动创建，需要检查服务端配置

2. 支持两种重试，参考[Spring Kafka：Retry Topic、DLT 的使用与原理 - 知乎](https://zhuanlan.zhihu.com/p/554967177)

   

