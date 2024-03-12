package com.soulmate.model;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.activerecord.generator.TypeMapping;
import com.jfinal.plugin.druid.DruidPlugin;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 本 demo 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: https://jfinal.com/club
 * <p>
 * 在数据库表有任何变动时，运行一下 main 方法，极速响应变化进行代码重构
 */
public class JFinalGenerator {
    static Prop p;


    static void loadConfig() {
        if (p == null) {
            String configFileName = "config.txt";
            p = PropKit.use(configFileName);
        }
    }


    public static DataSource getDataSource(String db, String user, String password) {
        DruidPlugin druidPlugin = new DruidPlugin(db, user, password);
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }


    public static void run(String dbName) {
        String host = p.get(dbName + ".host") == null ? "localhost" : p.get(dbName + ".host");
        String port = p.get(dbName + ".port") == null ? "3306" : p.get(dbName + ".port");
        String dbUser = p.get(dbName + ".dbUser");
        String dbPassword = p.get(dbName + ".dbPassword");
        if (dbUser == null || dbPassword == null) {
            throw new RuntimeException(dbName + ".dbUser or " + dbName + ".dbPassword" + " is null");
        }

        String dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?characterEncoding=utf8&useSSL=false&zeroDateTimeBehavior=convertToNull";


        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = p.get("modelPath") + "." + dbName;

        // base model 所使用的包名
        String baseModelPackageName = modelPackageName + ".base";

        // base model 文件保存路径
        String dir = System.getProperty("user.dir");
        String baseModelOutputDir = dir + "/src/main/java/" + baseModelPackageName.replace('.', '/');

        System.out.println("输出路径：" + baseModelOutputDir);

        // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
        String modelOutputDir = baseModelOutputDir + "/..";

        // 创建生成器
        Generator generator = new Generator(getDataSource(dbUrl, dbUser, dbPassword), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);

        // 配置是否生成备注
        generator.setGenerateRemarks(true);

        // 设置数据库方言
        generator.setDialect(new MysqlDialect());

        // 设置是否生成链式 setter 方法，强烈建议配置成 false，否则 fastjson 反序列化会跳过有返回值的 setter 方法
        generator.setGenerateChainSetter(true);

        // 添加不需要生成的表名到黑名单
//        generator.addBlacklist("adv");

        // 设置是否在 Model 中生成 dao 对象
        generator.setGenerateDaoInModel(true);

        // 设置是否生成字典文件
        generator.setGenerateDataDictionary(false);

        // 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "user"而非 Oscuser
        generator.setRemovedTableNamePrefixes("");

        // 将 mysql 8 以及其它原因之下生成 jdk 8 日期类型映射为 java.util.Date，便于兼容老项目，也便于习惯使用 java.util.Date 的同学
        TypeMapping tm = new TypeMapping();
        tm.addMapping(LocalDateTime.class, Date.class);
        tm.addMapping(LocalDate.class, Date.class);
        // tm.addMapping(LocalTime.class, LocalTime.class);		// LocalTime 暂时不变
        generator.setTypeMapping(tm);

        generator.setMappingKitClassName(dbName + "_MappingKit");

        // 生成
        generator.generate();
    }


    public static void main(String[] args) {
        loadConfig();

        String dbNameList = p.get("dbNameList");
        String[] split = dbNameList.split(",");
        for (String dbName : split) {
            try {
                run(dbName);
            } catch (Exception e) {
//                throw new RuntimeException(e);
                e.printStackTrace();
            }
        }
    }
}




