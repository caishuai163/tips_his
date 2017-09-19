# 新浪游戏H5接入文档


## 1.用户登录
### 1.1.用户信息获取
在游戏入口地址中，传入如下参数：
uid 用户唯一id
access_token  用户授权token，用于后续调用其他接口


传递的参数，游戏可以直接使用，为了安全考虑，游戏方可以调用后端接口，验证uid和access_token是否合法，具体看1.2

### 1.2.用户信息校验接口（服务端）
http://m.game.weibo.cn/api/sdk/user/check.json
用于用户身份校验。
#### 支持格式
JSON（返回为json，请求为标准http form的格式）
#### 接口登录密钥signature key，请联系新浪游戏运营人员索取
#### HTTP请求方式
POST
#### 提交参数说明
参数字段 | 类型|说明 | 必选
--------|-------------|--------|---
suid|string       | 用户的编号  | true
appkey|string       | 当前游戏的key  | true
deviceid|string       | 设备id H5游戏无法获取，直接传H5即可  | true
token|string       | 用户身份标识 | true
signature|string       | 签名串，生成方法见1.3  | true

#### 返回参数说明
参数字段 | 类型|说明
------------| -------------|-------------
suid	|string|
token         |string |
usertype|string	|账号类型:1 快速试玩 2手机 3新浪通行证 99微博

#### 返回示例
```json
{
"suid": "3508971941",
"token": "tXvasFVztJrB7ebc06600eFVEXdB", 
"usertype":1
}
```

####测试示例

### 1.3 用户信息接口签名机制
####1、签名规则
```
a)参数signature不参与签名
b)值为空的参数，也需要参与签名
c)若参数中包含特殊字符,例如中文,&,%,@等, 需要在签名之后进行url_encode
d)目前仅支持md5方式签名
```
####2、签名步骤
```
a)将所有待签名参数按参数名排序(字母字典顺序，例如PHP的ksort()函数)
b)把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串，组成字符串A
c)signature_key是新浪游戏分配的登录密钥，请联系新浪游戏运营人员索取
d)将字符串A与signature_key，用英文竖杠进行连接, 得到字符串C，对字符串C取md5值，得到字符串D，D就是所需要的签名
```
####3、示例
```
/**
 * 生成签名结果
 * @param $params 已排序要签名的数组
 * @param $sina_secret 新浪游戏运营人员分配的登录密钥
 * return 签名结果字符串
 */
public static function buildRequestMysign($params, $signature_key)
{
	if (!isset($params['appkey'])) return '';
	if (empty($signature_key)) return '';
	if (isset($params['signature'])) unset($params['signature']);
	//将所有待签名参数按参数名排序
	ksort($params);
	
	//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串，组成字符串A
	$str_A = self::_createLinkstring($params);
	
	//将字符串A与signature_key，用英文竖杠进行连接, 得到字符串C
	$str_C = sprintf('%s|%s', $str_A, $signature_key);
	
	//对字符串C取md5值，得到字符串D，D就是所需要的签名
	$str_D = md5($str_C);
	
	return $str_D;
}

/**
 * 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
 * @param $para 需要拼接的数组
 * return 拼接完成以后的字符串
 */
private static function _createLinkstring($para)
{
	$arg = '';
	while (list ($key, $val) = each ($para)) {
		$arg .= $key.'='.$val.'&';
	}
	//去掉最后一个&字符
	$arg = substr($arg, 0, -1);
	
	//如果存在转义字符，那么去掉转义
	if(get_magic_quotes_gpc()){$arg = stripslashes($arg);}
	
	return $arg;
}
```

----------

## 2.支付
### 2.1.支付下单接口
接口地址：http://sng.sina.com.cn/payment/order/order_cashier
请求方式：GET
注： 由于在轻应用体系下，游戏是在iframe里的，所以调用时，需要用
window.top.location.href

#### HTTP请求方式
GET
#### 提交参数说明
参数字段 | 类型|说明 | 必选
--------|-------------|--------|---
appkey|string       | 开放平台给应用分配的appkey | true
access_token|string       | 用户身份标识 | true
amount|int      | 支付金额，单位 分  | true
uid|string       | 用户id  | true
subject|string       | 商品名称 | true
desc|string      | 商品描述，将会显示在支付页面，不能为空 | true
show_url|string      | 支付的前端回调，默认传入新浪方的游戏入口地址，如 http://apps.weibo.com/game/xxx/xxx | true
pt|string|透传参数,在支付回调中，会原样返回 | false
timestamp|string|下单时候的时间戳，如 1475912542 | true

#### 返回参数说明
```
下单成功后，直接跳转到支付页面。
```
####测试示例

### 2.2.回调接口由开发商提供
与上述各接口不同，回调接口由各家CP各自进行开发和部署，遵循一致的规范，供手机微游戏调用。
#### 接口加密密钥，请使用游戏的 appsecret
#### HTTP请求方式
GET
#### 提交参数说明
参数字段 | 类型|说明 | 必选
--------|-------------|--------|---
order_id|string       | 调用下单接口获取到的订单号 | true
amount|int      | 支付金额，单位 分  | true
order_uid|string       | 支付用户id  | true
source|string       | 应用的appkey | true
actual_amount|int      | 实际支付金额，单位 分 | true
pt|string|透传参数（该参数的有无决定于下单时有没有上传pt参数） | true
signature|string   |用于参数校验的签名，生成办法参考2.3 | true

#### 返回参数说明
```
http状态码应为200，返回结果为字符串OK，且必须是大写。
```
####测试示例

### 2.3.支付接口签名机制
####1、签名规则
```
a)参数signature不参与签名
b)用于计算时参数取值不要做urlencode。
c)根据pt参数的有无，决定是否加上pt参数
```
####2、签名步骤
```
a)将所有待签名参数按参数名排序(字母字典顺序，例如PHP的ksort()函数)
b)把数组所有元素，按照“参数|参数值”的模式用“|”字符拼接成字符串，组成字符串A
c)将字符串A与 appsecret，用英文竖杠进行连接, 得到字符串B，对字符串B取sha1值，得到字符串C，C就是所需要的签名
```
####3、示例
```
http://test.game.weibo.cn/paysys/pay.php?
order_id=sng22fEJvaEEkFJ&amount=600&order_uid=123456&source=3465&actual_amount=600&pt=739&signature=xxxxxx
则计算签名值的方法为：
sha1(“actual_amount|600|amount|600|order_id|sng22fEJvaEEkFJ|order_uid|123456|pt|739|source|3465|appsecret”)

/**
 * 生成签名结果
 * @param $para_sort 已排序要签名的数组
 * return 签名结果字符串
 */
public static function buildRequestMysign($secret)
{
	if (empty($_REQUEST)) return FALSE;
	if (isset($_REQUEST['signature'])) unset($_REQUEST['signature']);
	//将所有待签名参数按参数名排序
	ksort($_REQUEST);

	//把数组所有元素，按照“参数|参数值”的模式用“|”字符拼接成字符串，组成字符串A
    $str_A = '';
    foreach ($_REQUEST as $key => $value)
    {
        $str_A .= sprintf('%s|%s|', $key, $value);
    }

	//将字符串A与appsecret，用英文竖杠进行连接, 得到字符串B
	$str_B = $str_A . $secret;

	//对字符串B取sha1值，得到字符串C，C就是所需要的签名
	$str_C = sha1($str_B);

	return $str_C;
}
```

----------

## 3.微博必接功能

### 3.1 H5微博分享-JS方法

在页面引入JS文件 http://mg.games.sina.com.cn/kjava/sng/js/share_game.js

参数字段 |    类型   |        说明      | 必选
-------- |-----------|------------------|---
page_id  |string     |需要分享的page_id | 是
success  |function   |成功的回调函数    | 是
error    |function   |失败的回调函数    | 否

引入 http://mg.games.sina.com.cn/kjava/sng/js/share_game.js
js 中有两个方法
can_share  判断是否在微博环境中，可以使用微博分享
share_weibo  发起微博分享

使用示例如下:

![js示例](https://git.oschina.net/uploads/images/2017/0719/172611_13e736fd_483144.png "js.png")


        $(document).ready(function(){
        //判断是否为微博环境
        if(share_game.can_share()){
            //调用分享方法
            share_game.share_weibo({
            page_id:'1068031s2372357442s1079455599',//上线时，运营人员会给新的pageid，替换一下即可
            success:function(){
                    //成功回调-必写
                    alert('成功');
                    
                    },
                    error:function(){
                    //失败回调-不是必写
                     alert('失败');
                    
                    }
                   });
                    
                }else{
                alert('不是微博环境');
                
                }
            });


----------


### 3.2.客户端保存桌面
将游戏入口保存的手机桌面的功能，便于用户以后继续玩游戏。
拷贝sina_bridge.js 到自己工程，也可以将js方法直接复制到工程的js里，调用addShortcut 方法。在callbackFunction 方法中处理结果。
注：该功能开发测试时，请在最新版本的android微博客户端中测试。



 
----------

## 4.选接功能

### 4.1.接受客户端生命周期事件

拷贝sina_bridge.js 到自己工程，也可以将js方法直接复制到工程的js里
如果需要，如 退出游戏后对音乐的处理，可以重写下列几个方法， 

//界面不在游戏中回调处理：

function sngH5GameOnPause(){
  showInfos("游戏暂停");
}; 


//切回到游戏界面回调处理：

function sngH5GameOnResume(){
showInfos("游戏恢复");
};

//关闭游戏回调处理：

function sngH5GameOnDestory(){
showInfos("游戏关闭");
}; 

----------

### 4.2 h5创角 统计接口
##### get /api/sdk/h5game/gamestartnew.json
#### 提交参数说明
参数字段 | 类型|说明 | 必选
--------|-------------|--------|---
appkey  |string       |当前游戏的appkey | 是
suid    |string       |用户编号         | 是
signature	|string       |用于参数校验的签名，生成办法参考2.3     |是
deviceid  |string       |设备id      |是
ip |string          |ip地址     |是
channel |string          |渠道号     |否


#### 返回参数说明
参数字段 | 类型|说明
------------| -------------|-------------
res         |bool |true 或 false

#### 参会示例 json
```
正确
{
    res: true
}
signature 错误
{
request: "/api/sdk/h5game/gamestartnew.json",
error_code: 27568,
error: "授权信息不合法"
}
```

##### 请求示例
 
- 请求

```
    http://m.game.weibo.cn/api/sdk/h5game/gamestartnew.json?deviceid=abc123&suid=12343&appkey=5678&ip=123.123.123.123&channel=1000400001&signature=5cfd230726d1566a0bf318505a32c71b
```

- 示例参数

```
    deviceid:abc123
    suid:12343
    appkey:5678
    ip:123.123.123.123
    channel:1000400001
    signature:5cfd230726d1566a0bf318505a32c71b
```

### 4.3.分享接口
用户类型为99 的用户，即是微博授权的用户，这时获取的access_token，可以直接调用微博开放平台的相关接口，其他类型的用户隐藏此功能。
接口地址：https://api.weibo.com/2/statuses/share.json

文档地址： http://open.weibo.com/wiki/2/statuses/share

由于此接口要求是post请求，但jsonp是不支持post的，如果要再前端js端调用，建议在cp服务器上做代理接口，防止报跨域错误。
需要将参数放到body中，以post方式提交。

#### HTTP请求方式
POST
#### 提交参数说明
参数字段 | 类型|说明 | 必选
--------|-------------|--------|---
access_token|string       | 用户身份标识 | true
status |string      | 要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。可以将游戏的入口地址放到文本中，这样就可以展示为linkcard样式  | true
rip|string       | 开发者上报的操作用户真实IP，形如：211.156.0.1。  | false

#### 提交参数示例
access_token=2.00IzXbOBDWEp8E33d9e2ePy&status=testcontent,http://apps.weibo.com/1854424193/8rZqo5R0&rip=222.222.222.222


#### 返回参数说明
```
{
    "created_at": "Wed Oct 24 23:39:10 +0800 2012",
    "id": 3504801050130000,
    "mid": "3504801050130827",
    "idstr": "3504801050130827",
    "text": "定向分组内容。",
    "source": "新浪微博</a>",
    "favorited": false,
    "truncated": false,
    "in_reply_to_status_id": "",
    "in_reply_to_user_id": "",
    "in_reply_to_screen_name": "",
    "geo": {
        "type": "Point",
        "coordinates": [
            40.413467,
            116.646439
        ]
    },
    "user": {
        "id": 1902538057,
        "idstr": "1902538057",
        "screen_name": "张三",
        "name": "张三",
        "province": "11",
        "city": "8",
        "location": "北京 海淀区",
        "description": "做最受尊敬的互联网产品经理...",
        "url": "",
        "profile_image_url": "http://tp2.sinaimg.cn/1902538057/50/22817372040/1",
        "profile_url": "304270168",
        "domain": "shenbinzhu",
        "weihao": "304270168",
        "gender": "m",
        "followers_count": 337,
        "friends_count": 534,
        "statuses_count": 516,
        "favourites_count": 60,
        "created_at": "Sat Dec 25 14:12:35 +0800 2010",
        "following": false,
        "allow_all_act_msg": true,
        "geo_enabled": true,
        "verified": false,
        "verified_type": 220,
        "allow_all_comment": true,
        "avatar_large": "http://tp2.sinaimg.cn/1902538057/180/22817372040/1",
        "verified_reason": "",
        "follow_me": false,
        "online_status": 0,
        "bi_followers_count": 185,
        "lang": "zh-cn",
        "level": 7,
        "type": 1,
        "ulevel": 0,
        "badge": {
            "kuainv": {
                "level": 0
            },
            "uc_domain": 0,
            "enterprise": 0,
            "anniversary": 0
        }
    },
    "annotations": [
        {
            "aa": "cc"
        }
    ],
    "reposts_count": 0,
    "comments_count": 0,
    "attitudes_count": 0,
    "mlevel": 0,
    "visible": {
        "type": 3,
        "list_id": 3469454702570000
    }
}
```
####测试示例

----------



##5.附录

### 5.1.接口错误的返回格式
```json
{
	"request" : "/rank/show",
	"error_code" : "10013",
	"error" : "Invalid weibo user"
}
```
#### 返回参数说明
参数字段 | 类型|说明
------------| -------------|-------------
request	|string| 请求的url
error_code         |string |具体含义请参考http://open.weibo.com/wiki/Error_code
error|string	|错误的说明

错误码 | 含义 | 话术
-------|-------|-------
