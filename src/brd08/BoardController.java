package brd08;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/board/*")
public class BoardController extends HttpServlet {
    private static String ARTICLE_IMAGE_REPO =
            "/Users/seongju/Coding/JSP/Java_Web/file_repo";
    BoardService boardService;
    ArticleVO articleVO;

    public void init(ServletConfig config)
        throws ServletException{
        boardService = new BoardService();
        articleVO = new ArticleVO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        doHandle(request,response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        doHandle(request,response);
    }

    private void doHandle(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        //변수 설정
        String nextPage = "";
        HttpSession session;
        //형식 설정
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        //url 가져오기
        String action = request.getPathInfo();
        System.out.println("action:" + action);
        try{
            List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
            //  board/
            if(action==null){
                String _section = request.getParameter("section");
                String _pageNum = request.getParameter("pageNum");
                //1로 초기화
                int section = Integer.parseInt(
                        (
                                (_section==null)
                                        ?"1"
                                        :_section
                        )
                );
                int pageNum = Integer.parseInt(
                        (
                                (_pageNum==null)
                                        ?"1"
                                        :_pageNum
                        )
                );
                Map<String, Integer> pagingMap = new HashMap<String, Integer>();
                pagingMap.put("section",section);
                pagingMap.put("pageNum",pageNum);
                Map articlesMap = boardService.listArticles(pagingMap);
                articlesMap.put("section",section);
                articlesMap.put("pageNum", pageNum);
                request.setAttribute("articlesMap", articlesMap);
                nextPage = "/board07/listArticles.jsp";
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher(nextPage);
            dispatcher.forward(request, response);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
