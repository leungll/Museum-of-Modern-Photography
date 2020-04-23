package com.pandawork.web.controller.carousel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pandawork.common.entity.carousel.Carousel;
import com.pandawork.common.entity.nature.Nature;
import com.pandawork.common.entity.user.User;
import com.pandawork.core.common.exception.SSException;
import com.pandawork.core.common.log.LogClerk;
import com.pandawork.service.User.UserService;
import com.pandawork.service.carousel.CarouselService;
import com.pandawork.web.spring.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author:liangll
 * @Description:
 * @Date: 17:00 2018/8/3
 */
@Controller
@RequestMapping("/carousel")
public class CarouselController extends AbstractController {

    @Autowired
    CarouselService carouselService;

    @Autowired
    UserService userService;


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String carouselList(@RequestParam(value="pn",defaultValue="1")Integer pn,Model model) {
        try {
            PageHelper.startPage(pn,3);
            List<Carousel> carouselList = Collections.emptyList();
            List<User> list2 = Collections.emptyList();
            List<Nature> list3 = Collections.emptyList();
            carouselList = carouselService.listAllCarousel();
            list2 = userService.listAll();
            list3 = natureService.listAll();
            PageInfo<Carousel> page = new PageInfo<Carousel>(carouselList);
            model.addAttribute("page", page);
            //此即为foreach循环的item
            model.addAttribute("carouselList", carouselList);
            model.addAttribute("userList", list2);
            model.addAttribute("natureList", list3);
            return "carousel/home_carousel";
        } catch (SSException e) {
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
    }

    @RequestMapping(value = "/list2", method = RequestMethod.GET)
    public String carouselList2(Model model) {
        try {
            List<Carousel> list = Collections.emptyList();
            list = carouselService.listAllCarousel();
            //此即为foreach循环的item
            model.addAttribute("carouselList2", list);
            return "outer/main";
        } catch (SSException e) {
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
    }

    @RequestMapping("/add")
    public String addCarousel(Carousel carousel, HttpSession session, MultipartFile uploadFile, Model model) throws SSException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String date1 = sdf.format(date);
        String filename = date1 + uploadFile.getOriginalFilename();
        String leftPath = session.getServletContext().getRealPath("images");
        File file = new File(leftPath,filename);
        carousel.setPath("/images/" + filename);
        try {
            uploadFile.transferTo(file);
        } catch (Exception e){
            System.out.println("文件保存出错");
            e.printStackTrace();
        }
        carouselService.addCarousel(carousel);
//        model.addAttribute("lunbo.getPath()",lunbo.getPath());
        return "redirect:/carousel/list";
    }

    @RequestMapping(value = "del/{id}", method = RequestMethod.GET)
    public String delCarousel(@PathVariable("id") int id) {
        try {
            carouselService.delCarousel(id);
            //删除后还需重定向页面才可获取最新列表
            return "redirect:/carousel/list";
        } catch (SSException e) {
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
    }

    @RequestMapping(value = "/toUpdate/{id}",method = RequestMethod.GET)
    public String toUpdate(@PathVariable("id") int id , Model model){
        try{
            Carousel carousel = new Carousel();
            carousel = carouselService.selectById(id);
            model.addAttribute("carousel",carousel);
            return "carousel/update_carousel";
        }catch(SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
    }

    @RequestMapping(value = "/update/{id}",method = RequestMethod.POST)
    public String update(Carousel carousel , @PathVariable("id")int id, Model model){
        try{
            carousel.setId(id);
            carouselService.updateCarousel(carousel);
            model.addAttribute("carousel",carousel);
            return "redirect:/carousel/list";
        }catch(SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
    }

    @RequestMapping(value = "/update2/{id}",method = RequestMethod.POST)
    public String update2(Carousel carousel , @PathVariable("id")int id, Model model, @RequestParam("status") String status){
        try{
            carousel.setId(id);
            String stt = new String();
            System.out.println(status);
            if(status.equals("1")){
                stt = "Yes";
            }else if (status.equals("0")){
                stt = "No";
            }else{
                stt = "不是0和1";
            }
            carousel.setId(id);
            carousel.setStatus(stt);
            model.addAttribute("carousel",carousel);
            carouselService.updateCarousel(carousel);
            System.out.println(stt+"轮播状态为");
            return "redirect:/carousel/list";
        }catch(SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
    }
}
