package app;

import app.index.*;
import app.login.*;
import app.stock.*;
import app.user.*;
import app.util.*;
import static spark.Spark.*;
import static spark.debug.DebugScreen.*;

public class Application {

   // Declare dependencies
   public static StockDao stockDao;
   public static UserDao userDao;

   public static void main(String[] args) {

      // Instantiate your dependencies
      stockDao = new StockDao();
      userDao = new UserDao();

      // Configure Spark
      port(4567);
      staticFiles.location("/public");
      staticFiles.expireTime(600L);
      enableDebugScreen();

      // Set up before-filters (called before each get/post)
      before("*",                  Filters.addTrailingSlashes);
      before("*",                  Filters.handleLocaleChange);

      // Set up routes
      get(Path.Web.INDEX,          IndexController.serveIndexPage);        
      get(Path.Web.STOCKS,         StockController.fetchAllStocks);
      get(Path.Web.STOCKDATA, "application/json", StockController.fetchRawStockData);
      //get(Path.Web.ONE_STOCK,       StockController.fetchOneStock);
      get(Path.Web.LOGIN,          LoginController.serveLoginPage);
      post(Path.Web.LOGIN,         LoginController.handleLoginPost);
      post(Path.Web.LOGOUT,        LoginController.handleLogoutPost);
      // All other URLs return Not Found page
      get("*",                     ViewUtil.notFound);

      //Set up after-filters (called after each get/post)
      after(Path.Web.INDEX,        Filters.addGzipHeader);
      after(Path.Web.STOCKS,       Filters.addGzipHeader);
      after(Path.Web.LOGIN,        Filters.addGzipHeader);
      after(Path.Web.LOGOUT,       Filters.addGzipHeader);
      // Do not apply GZip filter to JSON data APIs like STOCKDATA!
   }
}
