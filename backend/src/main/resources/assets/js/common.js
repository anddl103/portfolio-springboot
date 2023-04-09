const darkMode = document.getElementById('darkMode');



window.addEventListener('load', function () {
  if (darkMode) {
    initTheme();
    darkMode.addEventListener('change', function () {
      resetTheme();
    });
  }
});

const initTheme = (from) => {
    console.log(from)
    var darkThemeSelected = localStorage.getItem('darkMode') !== null && localStorage.getItem('darkMode') === 'dark';
    darkMode.checked = darkThemeSelected;
    darkThemeSelected ?
        document.body.setAttribute('data-theme', 'dark') :
        document.body.removeAttribute('data-theme');
}

const resetTheme = (from) => {
    if (from == 'app' ) {
        document.body.setAttribute('data-theme', 'dark');
        localStorage.setItem('darkMode', 'dark');
    } else {
        if (isMobile()) {
            document.body.setAttribute('data-theme', 'dark');
            localStorage.setItem('darkMode', 'dark');
        } else {
            document.body.removeAttribute('data-theme');
            localStorage.removeItem('darkMode');
        }
    }
}

const isMobile = () => {
    const user = navigator.userAgent;
    let is_mobile = false;
    if( user.indexOf("iPhone") > -1 ||
        user.indexOf("Android") > -1 ||
        user.indexOf("iPad") > -1 ||
        user.indexOf("iPod") > -1 ) {
        is_mobile = true;
    }
    return is_mobile;
}

const resToken = (token) => {
    api.token = token;
    if (typeof main !== 'undefined') {
        if (typeof main.getList !== 'undefined') {
            main.getList();
        }
    }

    if (typeof main !== 'undefined') {
        if (typeof main.getDetail() !== 'undefined') {
            main.getDetail();
        }
    }
}

const back = () => {
    window.history.back()
}

const getToken = async () => {
    appHandlerPostMessage('req_token')
}

const appHandlerPostMessage = (str) => {
    if (typeof AppHandler !== 'undefined') {
        AppHandler.postMessage(str)
    }
}

const getDate = (date) => {
  const time = new Date(date);
  time.setHours(time.getHours() + 9);
  return time.toISOString().replace('T', ' ').substring(0, 19);
}

const fullContent = document.querySelector('.infinite'); // 전체를 둘러싼 컨텐츠 정보획득
const screenHeight = screen.height; // 화면 크기

const api = {
  token : '',
  call(method, url, param, lang, callback, isToken=false) {

      console.log("method : " + method)

      $.ajax({
          type: method,
          url: url,
          dataType:'json',
          beforeSend : function(xhr){
              xhr.setRequestHeader("Content-type","application/json;charset=utf-8");
              xhr.setRequestHeader("Accept-Language", lang);
              if (isToken) {
                  xhr.setRequestHeader("x-access-token", api.token);
              }
          },
          timeout : 6000,
          data: (method == "POST" ? JSON.stringify(param) : param),
          success: function(data) {
              return callback(data);
          },
          error: function(error) {
              appHandlerPostMessage('err:'+JSON.stringify(error))
          }
      });
  },
};

(function(){

})(jQuery)




// 호출은 아래처럼
// apiCall(type, url, param, "en", function(data) {
//   console.log(data)
// });