package brd08;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.print.Printable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
                //페이징 파라미터 가져오기
                String _section = request.getParameter("section");
                String _pageNum = request.getParameter("pageNum");
                //1로 초기화
                int section = Integer.parseInt(((_section==null)?"1":_section));
                int pageNum = Integer.parseInt(((_pageNum==null)?"1":_pageNum));
                //맵으로 페이징 파라미터 만들기
                Map<String, Integer> pagingMap = new HashMap<String, Integer>();
                pagingMap.put("section",section);
                pagingMap.put("pageNum",pageNum);
                //Service 로 맵 전달
                Map articlesMap = boardService.listArticles(pagingMap);
                articlesMap.put("section",section);
                articlesMap.put("pageNum", pageNum);
                request.setAttribute("articlesMap", articlesMap);
                nextPage = "/board07/listArticles.jsp";
            } else if (action.equals("/listArticles.do")){
                //paging 관련 파라미터 가져오기
                String _section = request.getParameter("section");
                String _pageNum = request.getParameter("pageNum");

                //paging 파라미터 비어있으면 1 로 초기화, int 로 변경
                int section = Integer.parseInt(((_section==null)?"1":_section));
                int pageNum = Integer.parseInt(((_pageNum==null)?"1":_pageNum));

                //Map 에 paging 파라미터 넣기
                Map<String, Integer> pagingMap = new HashMap<String, Integer>();
                pagingMap.put("section", section);
                pagingMap.put("pageNum", pageNum);

                //service 로 맵 전달해서 리스트 받기
                Map articlesMap = boardService.listArticles(pagingMap);
                articlesMap.put("section", section);
                articlesMap.put("pageNum", pageNum);

                request.setAttribute("articlesMap", articlesMap);

                //다음 페이지
                nextPage = "/board07/listArticles.jsp";
            } else if (action.equals("/articleForm.do")){
                nextPage = "/board07/articleForm.jsp";
            } else if (action.equals("/addArticle.do")){
                int articleNO = 0;
                Map<String, String> articleMap = upload(request, response);
                String title = articleMap.get("title");
                String content = articleMap.get("content");
                String imageFileName = articleMap.get("imageFileName");

                articleVO.setParentNO(0);
                articleVO.setId("hong");
                articleVO.setTitle(title);
                articleVO.setContent(content);
                articleVO.setImageFileName(imageFileName);
                articleNO = boardService.addArticle(articleVO);
                if (imageFileName != null && imageFileName.length() != 0){
                    File srcFile = new File(ARTICLE_IMAGE_REPO + "/temp/" + imageFileName);
                    File destDir = new File(ARTICLE_IMAGE_REPO + "/" + articleNO);
                    destDir.mkdirs();
                    FileUtils.moveFileToDirectory(srcFile, destDir, true);
                }
                PrintWriter pw = response.getWriter();
                pw.print("<script> alert('데이터가 추가되었습니다'); location.href='" + request.getContextPath()
                        + "/board/listArticles.do';" + "</script>"
                );
                return;
            } else if (action.equals("/viewArticle.do")){
                String articleNO = request.getParameter("articleNO");
                articleVO = boardService.viewArticle(Integer.parseInt(articleNO));
                request.setAttribute("article",articleVO);
                nextPage = "/board07/viewArticle.jsp";
            } else if (action.equals("/modArticle.do")){
                Map<String, String> articleMap = upload(request, response);
                int articleNO = Integer.parseInt(articleMap.get("articleNO"));
                articleVO.setArticleNO(articleNO);
                String title = articleMap.get("title");
                String content = articleMap.get("content");
                String imageFileName = articleMap.get("imageFileName");
                articleVO.setParentNO(0);
                articleVO.setId("hong");
                articleVO.setTitle(title);
                articleVO.setContent(content);
                articleVO.setImageFileName(imageFileName);
                boardService.modArticle(articleVO);
                if (imageFileName != null && imageFileName.length() != 0){
                    String originalFileName = articleMap.get("originalFileName");
                    File srcFile = new File(ARTICLE_IMAGE_REPO + "/temp/" + imageFileName);
                    File destDir = new File(ARTICLE_IMAGE_REPO + "/" + articleNO);
                    destDir.mkdirs();
                    FileUtils.moveFileToDirectory(srcFile, destDir, true);
                    File oldFile = new File(ARTICLE_IMAGE_REPO + "/" + articleNO+ "/" + originalFileName);
                    oldFile.delete();
                }
                PrintWriter pw = response.getWriter();
                pw.print("<script>" + " alert('수정되었습니다');" + " location.href='"
                        + request.getContextPath() + "/board/viewArticle.do?articleNO=" + articleNO + "';"
                        + "</script>"
                        );
                return;
            } else if (action.equals("/removeArticle.do")){
                int articleNO = Integer.parseInt(request.getParameter("articleNO"));
                List<Integer> articleNOList = boardService.removeArticle(articleNO);
                for (int _articleNO : articleNOList){
                    File imgDir = new File(ARTICLE_IMAGE_REPO + "/" + articleNO);
                    if (imgDir.exists()){
                        FileUtils.deleteDirectory(imgDir);
                    }
                }

                PrintWriter pw = response.getWriter();
                pw.print("<script> alert('삭제했습니다.'); " +
                        "location.href='" + request.getContextPath() + "/board/listArticles.do';" +
                        "</script>"
                );
                return;
            } else if (action.equals("/replyForm.do")){
                int parentNO = Integer.parseInt(request.getParameter("parentNO"));
                session = request.getSession();
                session.setAttribute("parentNO", parentNO);
                nextPage = "/board07/replyForm.jsp";
            } else if (action.equals("/addReply.do")){
                session = request.getSession();
                int parentNO = (Integer) session.getAttribute("parentNO");
                session.removeAttribute("parentNO");
                Map<String, String> articleMap = upload(request, response);
                String title = articleMap.get("title");
                String content = articleMap.get("content");
                String imageFileName = articleMap.get("imageFileName");
                articleVO.setParentNO(parentNO);
                articleVO.setId("lee");
                articleVO.setTitle(title);
                articleVO.setContent(content);
                articleVO.setImageFileName(imageFileName);
                int articleNO = boardService.addReply(articleVO);
                if (imageFileName != null && imageFileName.length() != 0 ){
                    File srcFile = new File(ARTICLE_IMAGE_REPO + "/temp/" + imageFileName);
                    File destDir = new File(ARTICLE_IMAGE_REPO + "/" + articleNO);
                    destDir.mkdirs();
                    FileUtils.moveFileToDirectory(srcFile, destDir, true);
                }
                PrintWriter pw = response.getWriter();
                pw.print("<script> alert('답글이 추가되었습니다.'); "
                        + " location.href='" + request.getContextPath() + "/board/viewArticle.do?articleNO=" + articleNO + "';"
                        + " </script>"
                );
                return;
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher(nextPage);
            dispatcher.forward(request, response);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Map<String, String> upload(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        Map<String, String> articleMap = new HashMap<>();
        String encoding = "utf-8";
        File currentDirPath = new File(ARTICLE_IMAGE_REPO);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setRepository(currentDirPath);
        factory.setSizeThreshold(1024*1024);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try{
            List items = upload.parseRequest(request);
            for (int i = 0; i<items.size(); i++){
                FileItem fileItem = (FileItem) items.get(i);
                if (fileItem.isFormField()){
                    System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
                    articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
                } else {
                    System.out.println("파라미터이름:" + fileItem.getFieldName());
                    System.out.println("파일이름:" + fileItem.getName());
                    System.out.println("파일크기:" + fileItem.getSize() + "bytes");

                    if (fileItem.getSize() > 0){
                        int idx = fileItem.getName().lastIndexOf("\\");
                        if (idx == -1){
                            idx = fileItem.getName().lastIndexOf("/");
                        }

                        String fileName = fileItem.getName().substring(idx + 1);
                        articleMap.put(fileItem.getFieldName(), fileName);
                        File uploadFile = new File(currentDirPath + "/temp/" + fileName);
                        fileItem.write(uploadFile);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return articleMap;
    }
}
