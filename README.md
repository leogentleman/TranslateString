# TranslateString
一个用于快速添加国际化字符串的Android Studio 插件

## 使用方法
1. 在Android Studio的代码文件中，选中某个字符串key，右键弹出菜单（或者使用快捷键shit+command+x），选择**Translate String**
2. 在弹出的对话框中输入想要国际化的字符串（我们一般是先写成中文，再翻译成英文和繁体）
3. 点击确定，随后输入的字符串将被翻译成英文和繁体直接添加对应模块的字符串文件中去

## 注意事项
1. 由于使用了百度翻译的api，因此必须要联网才能使用，否则无法添加成功
2. 不同版本的Android Studio可能存在兼容性问题，如果存在请反馈或者commit

## 参考链接
### [ Android Studio插件开发实践--从创建到发布](http://blog.csdn.net/liuloua/article/details/51917362)
### [官方开发教程](http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started.html)
