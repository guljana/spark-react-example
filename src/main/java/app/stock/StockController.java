package app.stock;

import app.login.*;
import app.util.*;
import spark.*;
import java.util.*;
import static app.Application.stockDao;
import static app.util.JsonUtil.*;
import static app.util.RequestUtil.*;

public class StockController {

   public static Route fetchAllStocks = (Request request, Response response) -> {
      LoginController.ensureUserIsLoggedIn(request, response);
      if (clientAcceptsHtml(request)) {
         HashMap<String, Object> model = new HashMap<>();
         model.put("stocks", stockDao.getAllStocks());
         return ViewUtil.render(request, model, Path.Template.STOCKS_ALL);
      }
      if (clientAcceptsJson(request)) {
         return dataToJson(stockDao.getAllStocks());
      }
      return ViewUtil.notAcceptable.handle(request, response);
   };

   public static Route fetchRawStockData = (Request request, Response response) -> {
      // TODO: validate JS session!
      //LoginController.ensureUserIsLoggedIn(request, response);
      if (clientAcceptsJson(request)) {
         HashMap<String, List<PriceData>> data = new HashMap<String, List<PriceData>>();
         Iterable<Stock> stocks = stockDao.getAllStocks();
         for (Stock stock : stocks) {
            String symbol = stock.getSymbol();
            data.put(symbol, stockDao.getPriceHistoryForStock(symbol, null, null)); // TODO: use dates from request
         }
         response.header("Content-Type", "application/json");
         return JsonUtil.dataToJson(data);
      }
      return ViewUtil.notAcceptable.handle(request, response);
   };

   //    public static Route fetchOneStock = (Request request, Response response) -> {
   //        LoginController.ensureUserIsLoggedIn(request, response);
   //        if (clientAcceptsHtml(request)) {
   //            HashMap<String, Object> model = new HashMap<>();
   //            Stock stock = stockDao.getStockBySymbol(getParamIsbn(request));
   //            model.put("stock", stock);
   //            return ViewUtil.render(request, model, Path.Template.STOCKS_ONE);
   //        }
   //        if (clientAcceptsJson(request)) {
   //            return dataToJson(stockDao.getStockBySymbol(getParamIsbn(request)));
   //        }
   //        return ViewUtil.notAcceptable.handle(request, response);
   //    };
}
