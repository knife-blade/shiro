package com.touchealth.platform.processengine.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.touchealth.platform.processengine.controller.BaseController;
import com.touchealth.platform.processengine.entity.BaseEntity;
import com.touchealth.platform.processengine.service.BaseService;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CodeGenerator {

  /**
   * 说明表前缀，生成实体类和服务类时，名称将会过滤掉前缀。
   */
  private static final String[] TABLE_PREFIX = new String[] {
          "module_"
  };
  /**
   * 要生成Dao和Service类的实体表名
   */
  private static final String[] TABLE_NAMES = new String[] {
//          "t_example"
          "module_personal_info",
          "module_order_management",
          "module_my_mod"
  };
  /**
   * 生成类的作者注解
   */
  private static final String AUTHOR = "lvx";

  public static void main(String[] args) {
    AutoGenerator mpg = new AutoGenerator();
    GlobalConfig gc = new GlobalConfig();
    File file = new File("generator");
    String path = file.getAbsolutePath();
    path = path.substring(0, path.lastIndexOf(File.separator));
    gc.setOutputDir(path + "/src/main/java");
    gc.setFileOverride(true);
    gc.setActiveRecord(false);
    gc.setEnableCache(false);
    gc.setBaseResultMap(true);
    gc.setBaseColumnList(true);

    gc.setAuthor(AUTHOR);
    gc.setMapperName("%sDao");
    gc.setXmlName("%sDao");
    gc.setServiceName("%sService");
    gc.setServiceImplName("%sServiceImpl");
    gc.setControllerName("%sController");
    mpg.setGlobalConfig(gc);

    DataSourceConfig dsc = new DataSourceConfig();
    dsc.setDbType(DbType.MYSQL);
	dsc.setDriverName("com.mysql.cj.jdbc.Driver");
	dsc.setUrl("jdbc:mysql://192.168.137.199:3306/process_engine?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull");
	dsc.setUsername("dev");
	dsc.setPassword("of8jUtpVBUGvcF1uctU=");
    mpg.setDataSource(dsc);

//    String[] tables = new String[] {"t_example"};
    StrategyConfig strategy = new StrategyConfig();
    strategy.setNaming(NamingStrategy.underline_to_camel);
    strategy.setTablePrefix(TABLE_PREFIX);
    strategy.setInclude(TABLE_NAMES);
    strategy.setEntityLombokModel(true);
    strategy.setSuperControllerClass(BaseController.class);
    strategy.setSuperServiceClass(BaseService.class);
    strategy.setSuperServiceImplClass(BaseServiceImpl.class);
    strategy.setSuperEntityClass(BaseEntity.class);
    strategy.setSuperEntityColumns("id","created_by","updated_by","created_time","updated_time","deleted_flag");
    mpg.setStrategy(strategy);

    // 包配置
    PackageConfig pc = new PackageConfig();
    pc.setController("controller");
    pc.setEntity("entity");
    pc.setParent("com.touchealth.platform.processengine");
    pc.setMapper("dao");
    pc.setService("service");
    pc.setServiceImpl("service.impl");
    //pc.setXml("mapper");
    mpg.setPackageInfo(pc);

    // 自定义配置
    InjectionConfig cfg = new InjectionConfig() {
      @Override
      public void initMap() {
        // to do nothing
      }
    };
    // 如果模板引擎是 freemarker
    String templatePath = "/templates/mapper.xml.ftl";
    // 自定义输出配置
    List<FileOutConfig> focList = new ArrayList<>();
    // 自定义配置会被优先输出
    String finalPath = path;
    focList.add(new FileOutConfig(templatePath) {
      @Override
      public String outputFile(TableInfo tableInfo) {
        // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
        return finalPath + "/src/main/resources/mapper/" + pc.getModuleName()
                + "/" + tableInfo.getEntityName() + "Dao" + StringPool.DOT_XML;
      }
    });
    cfg.setFileOutConfigList(focList);
    mpg.setCfg(cfg);

    // 配置模板
    TemplateConfig templateConfig = new TemplateConfig();
    templateConfig.setXml(null);
    templateConfig.setController(null);
    mpg.setTemplate(templateConfig);
    mpg.setTemplateEngine(new FreemarkerTemplateEngine());
    mpg.execute();
  }
}
