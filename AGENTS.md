# Data Magic Web App

## Requirements

我要开发一个数据变形的web app，应用能够定时自动扫描本地指定目录下的文件，当文件名中包含`_SJBX_`的特征时，打开文件，一行一行读取文件内容。
文件的格式是一行一条数据，数据字段之间以 char(27) 分割。依次读取每个字段的内容，当字段的值以 `_MASK1`, `_MASK2`, `_MASK3` 等结尾时，根据配置的规则将数据进行变形，然后去除后缀。最后将变形后的数据以特定格式写入目标文件。
数据全部写入目标文件后，将目标文件上传的 ftp 服务器，并删除本地的源文件。
所有的变形记录，数据文件（源文件，目标文件），数据条数，变形字段数 以及变形开始时间，完成时间，写入数据库，前端页面可以查询相应记录。

## Project Structure

 - `backend-api` 后端接口服务，Springboot应用
 - `frontend-web` 前端页面，Vue3应用
 - `db-script` 数据库脚本，使用mysql数据库
 - `k8s-deploy` 部署yaml文件

## General Guidelines

 - 使用 git 来管理代码版本，每次编码之前先和远程仓库同步一下，获取最新版本和信息；开发完成一个 feature 或者 fix 一个 issue 之后要提交代码。
 - 应用的前后端主要通过 Restful Api 交互
 - 前后端最终都通过流水线打包成 docker 镜像，部署到k8s，因此前后端到代码中需要分别包含各自的 `Dockerfile`
 - 部署到 k8s 上时，前后端容器放在同一个 pod 中，通过 ingress 暴露服务

## Development Guidelines 

 - 当你设计数据库表结构，编写sql语句时，请加载 `@docs/mysql-guidelines.md`
 - 当你开发后端服务（即 Springboot 和 Java 程序时），请加载 `@docs/java-guidelines.md`, `@docs/springboot-guidelines.md`
 - 当你开发前端代码（即 Vue3 程序时），请加载 `@docs/frontend-guidelines.md`

