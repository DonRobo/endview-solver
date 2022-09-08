const l1y = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';

function a8w(q3k, w4s, shift, y1i, v9b, x2b, q4e) {
    shift = shift || 0;
    y1i = y1i || 0;
    q4e = q4e || 0;
    v9b = v9b || 0;
    x2b = x2b || 0;
    y1i |= 1;
    if (q3k === 1 && q4e === 1) {
        w4s = w4s.replace(/&/g, '+');
        w4s = w4s.replace(/=/g, '/');
    }
    var d9g = w4s.length;
    var o7x = Math.floor(d9g / 2);
    var g7f = [];
    if (d9g & 1) g7f[o7x] = w4s.charCodeAt(o7x);
    var l8v = 42;
    var j0t = 21;
    var k3g = shift;
    var i6d = y1i;
    for (var i = 0; i < o7x; i++) {
        var j9w = w4s.charAt(i);
        var x6t = w4s.charAt(d9g - i - 1);
        var j5r = l1y.indexOf(j9w);
        var b2x = l1y.indexOf(x6t);
        var i8l,
            y6x;
        if (j5r >= 0 && b2x >= 0) {
            if (q3k === 1) {
                i8l = (((j5r & l8v) | (b2x & j0t)) ^ v9b);
                y6x = (((b2x & l8v) | (j5r & j0t)) ^ x2b);
                i8l = (i8l + k3g) % 64;
                k3g += y1i;
                y6x = (y6x + k3g) % 64;
                k3g += y1i;
            } else {
                j5r = (j5r + 64 - k3g) % 64;
                k3g += y1i;
                b2x = (b2x + 64 - k3g) % 64;
                k3g += y1i;
                i8l = (((j5r ^ v9b) & l8v) | ((b2x ^ x2b) & j0t));
                y6x = (((b2x ^ x2b) & l8v) | ((j5r ^ v9b) & j0t));
            }
            g7f[i] = l1y.charCodeAt(i8l);
            g7f[d9g - i - 1] = l1y.charCodeAt(y6x);
        } else {
            g7f[i] = (j5r >= 0) ? l1y.charCodeAt(j5r ^ v9b) : j9w.charCodeAt(0);
            g7f[d9g - i - 1] = (b2x >= 0) ? l1y.charCodeAt(b2x ^ x2b) : x6t.charCodeAt(0);
        }
        i6d = (i6d + shift) % 64;
        v9b ^= i6d;
        x2b ^= i6d;
    }
    var s7i = '';
    for (var i = 0; i < d9g; i++) {
        s7i += String.fromCharCode(g7f[i]);
    }
    if (q3k == 2 && q4e == 1) {
        s7i = s7i.replace(/\+/g, '&');
        s7i = s7i.replace(new RegExp('/', 'g'), '=');
    }
    return s7i;
}

function p3z(c3m, q4e, c9y, q8n) {
    q4e = q4e || 3;
    c9y = c9y || '&';
    q8n = q8n || '=';
    var s7i;
    switch (q4e) {
        case 0:
            s7i = c3m;
            break;
        case 1:
            s7i = c3m.replace(/\+/g, '&');
            s7i = s7i.replace(new RegExp('/', 'g'), '=');
            break;
        case 2:
            s7i = this.l7x(c3m, c9y, q8n);
            break;
        case 3:
            s7i = j5q(c3m, c9y, q8n);
            break;
    }
    return s7i;
}

function j5q(a6l, c9y, q8n) {
    c9y = c9y || '&';
    q8n = q8n || '=';
    var p1u = 1,
        n5u = '';
    var c4f = l1y.indexOf(a6l.charAt(0));
    for (var i = 0; i < c4f; i++) {
        var t9y = l1y.indexOf(a6l.charAt(p1u));
        var n3f = l1y.indexOf(a6l.charAt(p1u + 1)) * 64 + l1y.indexOf(a6l.charAt(p1u + 2));
        n5u += a6l.substr(p1u + 3, t9y);
        if (n3f < 4095) {
            n5u += q8n + a6l.substr(p1u + 3 + t9y, n3f);
        } else {
            n3f = 0;
        }

        if (i < c4f - 1) {
            n5u += c9y;
        }

        p1u += 3 + t9y + n3f;
    }

    return n5u;
}

function decrypt(encrypted) {
    if (encrypted.substring(0, 5) === 'imit=') {
        encrypted = encrypted.substring(5);
    }

    if (encrypted.indexOf('&') >= 0 && encrypted.indexOf('=') >= 0) {
        return encrypted;
    }

    const l0 = l1y.indexOf(encrypted.charAt(0));
    const length = encrypted.length;
    const l1 = Math.max(l1y.indexOf(encrypted.charAt(length - 1)), 0) % 64;
    const l2 = Math.max(l1y.indexOf(encrypted.charAt(length - 3)), 0) % 64;
    const l3 = Math.max(l1y.indexOf(encrypted.charAt(length - 5)), 0) % 64;
    const l4 = l1y.charAt(l0 ^ ((777 - l1 + l2 + l3) % 64));

    switch (l4) {
        case 'A':
        case 'B':
            (function () {
                let start = 1;
                if (l4 !== 'A') {
                    let m9i = 0,
                        parity64 = l1y.indexOf(encrypted.charAt(start++));
                    for (let i = 7; i < encrypted.length; i++) {
                        m9i += Math.max(l1y.indexOf(encrypted.charAt(i)), 0) % 64;
                    }

                    if ((m9i & 63) !== parity64) return '';
                }

                let q4e = l1y.indexOf(encrypted.charAt(start++));
                let shift = l1y.indexOf(encrypted.charAt(start++));
                let y1i = l1y.indexOf(encrypted.charAt(start++));
                let v9b = l1y.indexOf(encrypted.charAt(start++));
                let x2b = l1y.indexOf(encrypted.charAt(start++));
                q4e = (q4e ^ Math.max(l1y.indexOf(encrypted.charAt(length - 2)), 0));
                shift = (shift ^ Math.max(l1y.indexOf(encrypted.charAt(length - 3)), 0));
                y1i = (y1i ^ Math.max(l1y.indexOf(encrypted.charAt(length - 4)), 0));
                v9b = (v9b ^ Math.max(l1y.indexOf(encrypted.charAt(length - 5)), 0));
                x2b = (x2b ^ Math.max(l1y.indexOf(encrypted.charAt(length - 6)), 0));
                q4e %= 4;
                encrypted = encrypted.substring(start);
                encrypted = a8w(2, encrypted, shift, y1i, v9b, x2b, 0);
                encrypted = p3z(encrypted, q4e);
            })();
            break;
        default:
            encrypted = '';
    }

    return encrypted;
}
