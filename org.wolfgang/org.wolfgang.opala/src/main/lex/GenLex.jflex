package org.wolfgang.opala.lexing.impl;

import java.io.IOException;
import java.net.URL;

import org.wolfgang.opala.lexing.GenericLexer;
import org.wolfgang.opala.lexing.Location;

%%
   
%class GenLex
%public
%unicode
%line
%column
%standalone

%{
    StringBuffer string   = new StringBuffer();
    URL          filename = null;

    public static GenericLexer getGenericLexer(URL filename,java.io.InputStream stream) {
        return new GenLex(filename, stream).getLexer();
    }

    private GenLex(URL filename,java.io.InputStream stream) {
       this(stream);
       this.filename = filename;
    }

    public GenericLexer getLexer() {
        return new GenericLexer() {
            public int getLexemeType() throws IOException {
                return yylex();
            }

            public String getLexemeValue() {
                return string.toString();
            }

            public Location getLocation() {
                return GenLex.this.getLocation();
            }

            public void finish() throws IOException {
                yyclose();
            }
        };
    }

    public void error() throws Error
    {
	throw new Error(filename+":"+(yyline+1)+": syntax error");
    }

    public Location getLocation() {
    	return new LocationImpl(filename,yycolumn+1,yyline+1);
    }

    public int token(int i) {
        if (i != Tokens._CHARACTERS_ && i != Tokens._STRING_ && i != Tokens._ERROR_ && i != Tokens._COMMENT_) {
            this.string.setLength(0);
            this.string.append(yytext());
        }

        return i;
    }
%}

CharsDelimiter  = '
StringDelimiter = \"
LineComment     = \/\/

BlockComment    = \/\*\*?
WhiteSpaces     = ([ ]|\t|\f|\r)+
NewLine         = (\n|\r\n)
NumberLitterals = [-]?([0-9]+|(0[xX][0-9a-fA-F]+)|([bB][01]+)|([oO][0-7]+))
Identifiers     = [:jletter:][[:jletterdigit:]_$]*
Separators      = [<>,(){}\[\];]
Operators       = [@%?:&#*+=~!/_|.\^\$;-]+|<=|=>|<>|->|<-
Special         = .

%state STRING, CHARACTERS, LINECOMMENT, BLOCKCOMMENT

%%
<YYINITIAL> {
    {CharsDelimiter}        { string.setLength(0); yybegin(CHARACTERS); }
    {StringDelimiter}       { string.setLength(0); yybegin(STRING); }
    {LineComment}           { string.setLength(0); yybegin(LINECOMMENT); }
    {BlockComment}          { string.setLength(0); yybegin(BLOCKCOMMENT); }
    {WhiteSpaces}           { return token(Tokens._SPACES_);  }
    {NewLine}               { return token(Tokens._EOL_);  }
    {Separators}            { return token(Tokens._OPERATOR_); }
    {Operators}             { return token(Tokens._OPERATOR_); }
    {Identifiers}           { return token(Tokens._IDENT_);    }
    {NumberLitterals}       { return token(Tokens._INTEGER_); }
    {Special}               { return token(Tokens._SPECIAL_); }
    <<EOF>>                 { return token(Tokens._EOF_); }
}

<STRING> {
    {StringDelimiter}       { yybegin(YYINITIAL); return token(Tokens._STRING_); }
    \\{StringDelimiter}     { string.append('\"'); }
    \\		                { string.append(yytext()); }
    [^\"\\]+                { string.append(yytext()); }
    <<EOF>>                 { yybegin(YYINITIAL); string.append(" <...>"); return token(Tokens._ERROR_); }
}

<CHARACTERS> {
    {CharsDelimiter}        { yybegin(YYINITIAL); return token(Tokens._CHARACTERS_); }
    \\{CharsDelimiter}      { string.append('\''); }
    \\  		            { string.append(yytext()); }
    [^\'\\]+                { string.append(yytext()); }
    <<EOF>>                 { yybegin(YYINITIAL); string.append(" <...>"); return token(Tokens._ERROR_); }
}

<LINECOMMENT> {
    {NewLine}               { yybegin(YYINITIAL); return token(Tokens._COMMENT_); }
    [\r]                    { string.append(yytext()); }
    [^\r\n]+                { string.append(yytext()); }
    <<EOF>>                 { yybegin(YYINITIAL); return token(Tokens._COMMENT_); }
}

<BLOCKCOMMENT> {
    \*\/                    { yybegin(YYINITIAL); return token(Tokens._COMMENT_); }
    \*                      { string.append(yytext()); }
    [^\*]+                  { string.append(yytext()); }
    <<EOF>>                 { yybegin (YYINITIAL); string.append(" <...>"); return token(Tokens._ERROR_); }
}
