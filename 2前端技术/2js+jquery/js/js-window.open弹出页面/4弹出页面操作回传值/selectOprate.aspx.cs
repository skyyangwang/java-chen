using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace WebForm.test
{
    public partial class selectOprate : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            Response.CacheControl = "no-cache";
            string Type = Request.QueryString["Type"];                              //取得参数以判断是否上传文件

            if (Type == "Upload")   //通过判断,在页面点击后处理里面的内容;
            {

                string str2 = cont1.Value;
                Response.Write("<script type=\"text/javascript\">alert('文件上传成功!');window.close();</script>");
                Response.Write("<script type=\"text/javascript\">window.opener.insertHTMLEdit('" + str2 + "');</script>");
            }
        }
    }
}
