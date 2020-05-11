#  H1
##  H2
###  H3
####  H4
#####  H5
######  H6
 
另外，H1和H2还能用以下方式显示：
一级标题
===
 
二级标题
---

*斜体* or _强调_
**加粗** or __加粗__
***粗斜体*** or ___粗斜体__
 
但是，如果你的 * 和 _ 两边都有空白的话，它们就只会被当成普通的符号：这是一段* 文本强调 *的说明示例。
如果要在文字前后直接插入普通的星号或底线，你可以用反斜线（转义符）：\*this text is surrounded by literal asterisks\*

Unordered 无序列表：
* 无序列表
* 子项
* 子项
 
+ 无序列表
+ 子项
+ 子项
 
- 无序列表
- 子项
- 子项
 
Ordered 有序列表：
1. 第一行
2. 第二行
3. 第三行
 
1. 第一行
- 第二行
- 第三行
 
组合：
* 产品介绍（子项无项目符号）
    此时子项，要以一个制表符或者4个空格缩进
 
* 产品特点
    1. 特点1
    - 特点2
    - 特点3
* 产品功能
    1. 功能1
    - 功能2
    - 功能3
 
可有时我们会出现这样的情况，首行内容是以日期或数字开头：2013. 公司年度目标。
为了避免也被转化成有序列表，我们可以在"."前加上反斜杠（转义符）：2013\. 公司年度目标。

Inline-style 内嵌方式：
[link text](https://www.google.com "title text")
 
Reference-style 引用方式：
[link text][id]
[id]: https://www.mozilla.org "title text"
 
Relative reference to a repository file 引用存储文件：
[link text](../path/file/readme.text "title text")
 
还能这样使用：
[link text][]
[link text]: http://www.reddit.com
 
Email 邮件：
<example@example.com>


Inline-style 内嵌方式：
![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "title text")
 
Reference-style 引用方式：
![alt text][logo]
[logo]: https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "title text"

标记一小段行内代码：
本文是一篇介绍`Markdown`的语法的文章
 
如果高亮的内容包含`号，可以这样写：
`` `包裹起来` ``
 
语法高亮：
```html
    <div>Syntax Highlighting</div>
```
```css
    body{font-size:12px}
```
 
```javascript
    var s = "JavaScript syntax highlighting";
    alert(s);
```
```php
    <?php
      echo "hello, world!";
    ?>
```
```python
    s = "Python syntax highlighting"
    print s
```

在该行开头缩进4个空格或一个制表符(tab)
 
<strong>Blockquotes 引用：</strong>
> Email-style angle brackets
> are used for blockquotes.
> > And, they can be nested.
> #### Headers in blockquotes
> * You can quote a list.
> * Etc.


在一行的结尾处加上2个或2个以上的空格，也可以使用</br>标签
第一行文字，
第二行文字

***
* * *
- - -

Markdown 可以利用反斜杠来插入一些在语法中有其它意义的符号，例如：如果你想要用星号加在文字旁边的方式来做出强调效果，你可以在星号的前面加上反斜杠：
\*literal asterisks\*
Markdown 支持以下这些符号前面加上反斜杠来帮助插入普通的符号：
\反斜杠  `反引号  *星号  _下划线  {}花括号  []方括号  ()括弧  #井字号  +加号  -减号  .英文句 !感叹号