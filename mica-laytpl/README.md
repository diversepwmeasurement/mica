# laytpl 模板（将其引入到 java 中）

具体可见 [《震惊，java8 Nashorn和laytpl居然能擦出这样火花！》](https://my.oschina.net/qq596392912/blog/872813)

## 注意
- jmh 实测性能不是很出色，约为 `Thymeleaf` 的 `1/2` 适合用于对性能不是特别高的场景。例如：代码生成等。
- java15 中会移除 Nashorn（[JEP 372:Remove the Nashorn JavaScript Engine](https://openjdk.java.net/projects/jdk/15/)）

### java15 之后需要添加 nashorn-core
```xml
<dependency>
  <groupId>org.openjdk.nashorn</groupId>
  <artifactId>nashorn-core</artifactId>
  <version>${version}</version>
</dependency>
```

## 添加依赖
### maven
```xml
<dependency>
  <groupId>net.dreamlu</groupId>
  <artifactId>mica-laytpl</artifactId>
  <version>${version}</version>
</dependency>
```

### gradle
```groovy
compile("net.dreamlu:mica-laytpl:${version}")
```

## 配置
| 配置项 | 默认值 | 说明 |
| ----- | ------ | ------ |
| mica.laytpl.cache | true | 缓存模板，默认：true |
| mica.laytpl.close | }} | 模板分隔符结束，默认：}} |
| mica.laytpl.date-pattern |  | Date 日期格式化，默认："yyyy-MM-dd HH:mm:ss" |
| mica.laytpl.local-date-pattern |  | java8 LocalDate日期格式化，默认："yyyy-MM-dd" |
| mica.laytpl.local-date-time-pattern |  | java8 LocalDateTime日期时间格式化，默认："yyyy-MM-dd HH:mm:ss" |
| mica.laytpl.local-time-pattern |  | java8 LocalTime时间格式化，默认："HH:mm:ss" |
| mica.laytpl.num-pattern | #.00 | 数字格式化，默认：#.00 |
| mica.laytpl.open | {{ | 模板分隔符开始，默认：{{ |
| mica.laytpl.prefix | classpath:templates/tpl/ | 模板前缀，默认：classpath:templates/tpl/ |

## 使用
```java
@Autowired
private MicaTemplate micaTemplate;
```

```java
Map<String, Object> data = new HashMap<>();
data.put("title", "mica");

String html = micaTemplate.render("<h3>{{ d.title }}</h3>", data);
```

```java
Map<String, Object> data = new HashMap<>();
data.put("title", "mica");

// renderTpl 将渲染 classpath:templates/tpl/ 下的模板文件
String html = micaTemplate.renderTpl("test.html", data);
```

## 模版语法

| 语法              | 说明                                                         | 
| ----------------- | ------------------------------------------------------------ |
| {{ d.field }}     | 输出一个普通字段，不转义html                                 |
| {{= d.field }}    | 输出一个普通字段，并转义html                                 |
| {{# JS表达式 }}    | JS 语句。一般用于逻辑处理。用分隔符加 # 号开头。注意：如果你是想输出一个函数，正确的写法是：{{ fn() }}，而不是：{{# fn() }} |
| {{! template !}}  | 对一段指定的模板区域进行过滤，即不解析该区域的模板。 |

## 内置对象
| 对象     | 说明    |
| ------- | ------- |
| console | 同 js console 可使用 console.log 打印日志，采用 Slf4j 做的嫁接 |
| fmt     | 格式化时间或者数字  fmt.format( d.date ))，或者 fmt.format( d.date, "yyyy-MM-dd" )) 自定义格式。|
| mica    | 使用 mica.use("sec") 在模板中使用 spring bean |

## 日志和格式
```html
{{#
console.log();

console.log("im {}", "L.cm");

console.error("hi im {}", "L.cm");

console.log("laytpl version:{}", laytpl.v);

console.log(fmt.format( d.date ));
}}
```

## 使用 Spring bean
```html
测试tpl中使用spring bean:

直接使用 mica.use("sec"); 方法传参beanName，即可。

示例：
<br>
{{# mica.use("sec").hasPermission('admin:add') }}
<br>
```

## 拓展阅读
巧用 java 调用 js，解决一些小问题。

### 使用场景
1. 执行爬虫登录中的加解密算法，部分 js 中的加解密算法和 java 的不能正常通用。

### 示例（巧妙解析 jsonp）
```java
public static void main(String[] args) throws ScriptException {
    String jsonp = "/**/callback( {\"client_id\":\"123\",\"openid\":\"123\",\"unionid\":\"123\"} )";

    // 构造 jsonp 的 function
    String jsFun = "function callback(json) { return json };";

    // 加载 js 引擎
    ScriptEngineManager engineManager = new ScriptEngineManager();
    ScriptEngine engine = engineManager.getEngineByMimeType("text/javascript");
    // 执行
    engine.eval(jsFun);

    // 读取结果
    Map json = (Map) engine.eval(jsonp);
    System.out.println(json);
}
```