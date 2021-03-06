$(function() {
    all_images(1, 9);

    $("#nav_bar span").addClass("page-info");


});

function all_images(num,size){

    $.ajax({
        url:'image/all',
        data:{pageNum:num,pageSize:size},
        method:'post',
        dataType:'json',
        success:function(data){
            $("#pageNum").text(data.pageNum);
            $("#pages").text(data.pages);
            $("#pageSize").text(data.pageSize);
            $("#total").text(data.total);
            $("#images_ul").empty();
            $("#nav_bar").show();
            $.each(data.row,function(i,img){
               var imgli = $("<li ></li>");
               var img_date_div = $("<div classs='img_date'></div>");
               var imgimg = $("<image classs='image-ul-show' onclick='lgimg(this)'/>");
               var url_text = $("<p id='name"+img.name+"' classs='image-create-date' hidden></p>");
                var small_url = getUrl(img.url,640,480);
               url_text.text(img.create_time);
               imgimg.attr("src",small_url);
               img_date_div.append(imgimg).append(url_text);
               imgli.append(img_date_div);
               $("#images_ul").append(imgli);

           })
        }
    })
}


function dateDiv(){
    var div = "<div classs='img_date'></div>";
    return div;
}

function find_page(){
    var pageSize = $("#in_pageSize").val();
    var pageNum = $("#in_pageNum").val();

    if(pageSize == '' || pageSize <=0){
        pageSize = 12;
    }
    if(pageNum == '' || pageNum <= 0 )
        pageNum = 1;
    all_images(pageNum,pageSize);
}

function pre_page(){
    var pageSize = 12;
    var pageNum = $("#pageNum").text();
    pageNum = pageNum>1?pageNum-1:pageNum;
    all_images(pageNum,pageSize);
}

function next_page(){
    var pageSize = 12;
    var pages = parseInt($("#pages").text());
    var pageNum = parseInt($("#pageNum").text());
    pageNum = pageNum<pages?pageNum+1:pageNum;
    all_images(pageNum,pageSize);
}

function getUrl(url,w,h){
    var a=  url.substring(0,115);
    var b= url.substring(119,122);
    var c = url.substring(126);
    return a+w+b+h+c;
}
function getBigUrl(url,w,h){
    var a=  url.substring(0,115);
    var b= url.substring(118,121);
    var c = url.substring(124);
    return a+w+b+h+c;
}

function getImgName(url){
    var id = url.substring(28,60);
    return id;
}
function lgimg(img){
    $("#back_pre_btn").show();
    var smallurl = img.src;
    var largeurl = getBigUrl(smallurl,1920,1080);
    show_table_image(largeurl);
    location.href="#image_info";
    $("#name"+getImgName(img.src)).show();
}

function show_table_image(image_url){
    $("#image_show").attr("src",image_url);
    $("#table_image_url").text(image_url);
}

function download_table_image(){
    location.href = "image/download?url="+$("#table_image_url").text();
}

