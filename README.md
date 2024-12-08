51topup outbound
=================

调用外部第三方充值模块接口，实现充值功能。

# Tech Stack

- Spring Reactive: WebFlux, WebClient

# Work Flow

- 从数据库中获取待充值的订单(2分钟一次)
- 调用外部充值模块接口
- 更新订单状态

开发中，同步数据的格式最好使用CSV，方便后续使用DuckDB等进行测试。

# 货源商列表

##  四象盈通

- URL: http://aa.wefutureidea.com/
- 商户ID:35132
