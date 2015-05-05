var regexps = [
               { regexp: /state/, type: "keywords"},
               { regexp: /trans/, type: "keywords"},
               { regexp: /\/\/.*$/, type: "comment"},
];

function token(stream, state) {
	var token = null;
	var buf = '';
    while ((c = stream.next()) != null && token == null) {
    	buf[] = c;
    	for (var re in regexps) {
    		if(re.regexp.test(buf)) {
    			return;
    		}
    	}
    }
}
