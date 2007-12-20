var NO_PROXY = "$NOPROXY_EXTS$";

function FindProxyForURL(url, host) {
  if( url.substring(0,5) == "http:" ) {
    if( NO_PROXY.length > 0 && url.indexOf("?") == -1 ) {
	  var extPos = url.lastIndexOf(".");
	  if( extPos != -1 ) {
	    if( NO_PROXY.indexOf("|"+url.substring(extPos+1).toLowerCase()+"|") != -1 ) {
	      return "$HTTP_DIRECT$";
	    }
	  }
    }
    return "$HTTP_PROXY$";
  }
  if( url.substring(0,6) == "https:" ) {
    return "$HTTPS_DIRECT$";
  }
  if( url.substring(0,4) == "ftp:" ) {
    return "$FTP_DIRECT$";
  }
  if( url.substring(0,7) == "gopher:" ) {
    return "$GOPHER_DIRECT$";
  }
  return "DIRECT";
}
