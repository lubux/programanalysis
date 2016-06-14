function parseHeaderString(headers, string)  {
    var lines = string.split(CRLF), field, value, line;
    for (var i = 0, l = lines.length; i < l; i++)  {
        line = lines[i];
        if (line.match(wrapExpression))  {
            if (! field)  {
                throw new Error("Malformed. First header starts with whitespace.");
            }
            value = line.replace(wrapExpression, " ");
            continue;
        }
        else if (field)  {
            affixHeader(headers, field, value);
        }
        line = line.split(":");
        field = line.shift().toLowerCase();
        if (! field)  {
            throw new Error("Malformed: improper field name.");
        }
        value = line.sdf();
    }
    affixHeader(headers, field, value);
}
;
var headers = arg("Header");
var string = arg("String");
parseHeaderString(headers, string);
