package com.axis2.util;


import com.axis2.pojo.Pixel;
import com.axis2.pojo.Point;
/**
 * BD Map Transformation
 * @author YUAN
 */
public class Transformation {
	final private double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
	final private double PI = 3.1415926535897932384626;
	final private double ER = 6378245.0;
	final private double ee = 0.00669342162296594323;
	final private  double[] tG = {1.289059486E7, 8362377.87, 5591021, 3481989.83, 1678043.12, 0};
	final private  double[][] rP = {{1.410526172116255E-8, 8.98305509648872E-6, -1.9939833816331, 200.9824383106796, -187.2403703815547, 91.6087516669843, -23.38765649603339, 2.57121317296198, -0.03801003308653, 1.73379812E7}, {-7.435856389565537E-9, 8.983055097726239E-6, -0.78625201886289, 96.32687599759846, -1.85204757529826, -59.36935905485877, 47.40033549296737, -16.50741931063887, 2.28786674699375, 1.026014486E7}, {-3.030883460898826E-8, 8.98305509983578E-6, 0.30071316287616, 59.74293618442277, 7.357984074871, -25.38371002664745, 13.45380521110908, -3.29883767235584, 0.32710905363475, 6856817.37}, {-1.981981304930552E-8, 8.983055099779535E-6, 0.03278182852591, 40.31678527705744, 0.65659298677277, -4.44255534477492, 0.85341911805263, 0.12923347998204, -0.04625736007561, 4482777.06}, {3.09191371068437E-9, 8.983055096812155E-6, 6.995724062E-5, 23.10934304144901, -2.3663490511E-4, -0.6321817810242, -0.00663494467273, 0.03430082397953, -0.00466043876332, 2555164.4}, {2.890871144776878E-9, 8.983055095805407E-6, -3.068298E-8, 7.47137025468032, -3.53937994E-6, -0.02145144861037, -1.234426596E-5, 1.0322952773E-4, -3.23890364E-6, 826088.5}};
	final private  double[] mB = { 12890594.86, 8362377.87, 5591021, 3481989.83, 1678043.12, 0};
	final private  int[] lB =  { 75, 60, 45, 30, 15, 0};
	final private  double[][] qG= {{ - 0.0015702102444, 111320.7020616939, 1704480524535203.0, -10338987376042340.0, 26112667856603880.0, -35149669176653700.0, 26595700718403920.0, -10725012454188240.0, 1800819912950474.0, 82.5}, {8.277824516172526E-4, 111320.7020463578, 6.477955746671607E8, -4.082003173641316E9, 1.077490566351142E10, -1.517187553151559E10, 1.205306533862167E10, -5.124939663577472E9, 9.133119359512032E8, 67.5}, {0.00337398766765, 111320.7020202162, 4481351.045890365, -2.339375119931662E7, 7.968221547186455E7, -1.159649932797253E8, 9.723671115602145E7, -4.366194633752821E7, 8477230.501135234, 52.5}, {0.00220636496208, 111320.7020209128, 51751.86112841131, 3796837.749470245, 992013.7397791013, -1221952.21711287, 1340652.697009075, -620943.6990984312, 144416.9293806241, 37.5}, { - 3.441963504368392E-4, 111320.7020576856, 278.2353980772752, 2485758.690035394, 6070.750963243378, 54821.18345352118, 9540.606633304236, -2710.55326746645, 1405.483844121726, 22.5}, { - 3.218135878613132E-4, 111320.7020701615, 0.00369383431289, 823725.6402795718, 0.46104986909093, 2351.343141331292, 1.58060784298199, 8.77738589078284, 0.37238884252424, 7.45}};
	final private  double[][] mL = new double[][] {
			new double[] {
					1.410526172116255e-008,
					8.983055096488720e-006,
					-1.99398338163310,
					2.009824383106796e+002,
					-1.872403703815547e+002,
					91.60875166698430,
					-23.38765649603339,
					2.57121317296198,
					-0.03801003308653,
					1.733798120000000e+007
			},
			new double[] {
					-7.435856389565537e-009,
					8.983055097726239e-006,
					-0.78625201886289,
					96.32687599759846,
					-1.85204757529826,
					-59.36935905485877,
					47.40033549296737,
					-16.50741931063887,
					2.28786674699375,
					1.026014486000000e+007
			},
			new double[] {
					-3.030883460898826e-008,
					8.983055099835780e-006,
					0.30071316287616,
					59.74293618442277,
					7.35798407487100,
					-25.38371002664745,
					13.45380521110908,
					-3.29883767235584,
					0.32710905363475,
					6.856817370000000e+006
			},
			new double[] {
					-1.981981304930552e-008,
					8.983055099779535e-006,
					0.03278182852591,
					40.31678527705744,
					0.65659298677277,
					-4.44255534477492,
					0.85341911805263,
					0.12923347998204,
					-0.04625736007561,
					4.482777060000000e+006
			},
			new double[] {
					3.091913710684370e-009,
					8.983055096812155e-006,
					0.00006995724062,
					23.10934304144901,
					-0.00023663490511,
					-0.63218178102420,
					-0.00663494467273,
					0.03430082397953,
					-0.00466043876332,
					2.555164400000000e+006
			},
			new double[] {
					2.890871144776878e-009,
					8.983055095805407e-006,
					-0.00000003068298,
					7.47137025468032,
					-0.00000353937994,
					-0.02145144861037,
					-0.00001234426596,
					0.00010322952773,
					-0.00000323890364,
					8.260885000000000e+005
			}
	};

	final private  double[][] lC = new double[][] {
			new double[] {
					-0.00157021024440,
					1.113207020616939e+005,
					1.704480524535203e+015,
					-1.033898737604234e+016,
					2.611266785660388e+016,
					-3.514966917665370e+016,
					2.659570071840392e+016,
					-1.072501245418824e+016,
					1.800819912950474e+015,
					82.5
			},
			new double[] {
					8.277824516172526e-004,
					1.113207020463578e+005,
					6.477955746671608e+008,
					-4.082003173641316e+009,
					1.077490566351142e+010,
					-1.517187553151559e+010,
					1.205306533862167e+010,
					-5.124939663577472e+009,
					9.133119359512032e+008,
					67.5
			},
			new double[] {
					0.00337398766765,
					1.113207020202162e+005,
					4.481351045890365e+006,
					-2.339375119931662e+007,
					7.968221547186455e+007,
					-1.159649932797253e+008,
					9.723671115602145e+007,
					-4.366194633752821e+007,
					8.477230501135234e+006,
					52.5
			},
			new double[] {
					0.00220636496208,
					1.113207020209128e+005,
					5.175186112841131e+004,
					3.796837749470245e+006,
					9.920137397791013e+005,
					-1.221952217112870e+006,
					1.340652697009075e+006,
					-6.209436990984312e+005,
					1.444169293806241e+005,
					37.5
			},
			new double[] {
					-3.441963504368392e-004,
					1.113207020576856e+005,
					2.782353980772752e+002,
					2.485758690035394e+006,
					6.070750963243378e+003,
					5.482118345352118e+004,
					9.540606633304236e+003,
					-2.710553267466450e+003,
					1.405483844121726e+003,
					22.5
			},
			new double[] {
					-3.218135878613132e-004,
					1.113207020701615e+005,
					0.00369383431289,
					8.237256402795718e+005,
					0.46104986909093,
					2.351343141331292e+003,
					1.58060784298199,
					8.77738589078284,
					0.37238884252424,
					7.45
			}
	};

	private Point bg(Point a) {
		double x = a.getLng() - 0.0065;
		double y = a.getLat() - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
		double t = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
		return new Point(z * Math.cos(t), z * Math.sin(t));
	}

	private Point gb(Point a) {
		double lng = a.getLng(), lat = a.getLat();
		double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
		double t = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
		return new Point(z * Math.cos(t) + 0.0065, z * Math.sin(t) + 0.006);
	}

	private Point wg(Point a) {
		double lng = a.getLng(), lat = a.getLat();
		if (oc(lng, lat)) {
			return new Point(lng, lat);
		} else {
			double dlat = tt(lng - 105.0, lat - 35.0);
			double dlng = tg(lng - 105.0, lat - 35.0);
			double r = lat / 180.0 * PI;
			double m = Math.sin(r);
			m = 1 - ee * m * m;
			double s = Math.sqrt(m);
			dlat = (dlat * 180.0) / ((ER * (1 - ee)) / (m * s) * PI);
			dlng = (dlng * 180.0) / (ER / s * Math.cos(r) * PI);
			return new Point(lng + dlng, lat + dlat);
		}
	}

	private Point gw(Point p) {
		double lng = p.getLng(), lat = p.getLat();
		if (oc(lng, lat)) {
			return new Point(lng, lat);
		} else {
			double dlat = tt(lng - 105.0, lat - 35.0);
			double dlng = tg(lng - 105.0, lat - 35.0);
			double r = lat / 180.0 * PI;
			double m = Math.sin(r);
			m = 1 - ee * m * m;
			double s = Math.sqrt(m);
			dlat = (dlat * 180.0) / ((ER * (1 - ee)) / (m * s) * PI);
			dlng = (dlng * 180.0) / (ER / s * Math.cos(r) * PI);
			double mglat = lat + dlat;
			double mglng = lng + dlng;
			return new Point(lng * 2 - mglng, lat * 2 - mglat);
		}
	}

	private double tt(double lng, double lat) {
		double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	private double tg(double lng, double lat) {
		double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
		return ret;
	}

	private  Point ak(Pixel a, int b, Point c, Pixel d) {
		double z = this.yc(b);
		Pixel m=cM(c);
		return Ab(new Point(Double.parseDouble(m.get_x()) + z * (a.getX() - d.getX() / 2), Double.parseDouble(m.get_y()) - z * (a.getY() - d.getY() / 2)));
	}

	private  Point Ab(Point a) {
		if (a == null) {
			return new Point(0, 0);
		}
		Point b;
		double[] c = new double[]{};
		b = new Point(Math.abs(a.getLng()), Math.abs(a.getLat()));
		for (int d = 0; d < tG.length; d++) {
			if (b.getLat() >= tG[d]) {
				c = rP[d];
				break;
			}
		}
		a = qK(a, c);
		if (a != null) {
			a.Round();
			return a;
		}
		return new Point(0, 0);
	}

	private  Point qK(Point a, double[] b) {
		if (a != null && b.length > 0) {
			double c = b[0] + b[1] * Math.abs(a.getLng());
			double d = Math.abs(a.getLat()) / b[9];
			d = b[2] + b[3] * d + b[4] * d * d + b[5] * d * d * d + b[6] * d * d * d * d + b[7] * d * d * d * d * d + b[8] * d * d * d * d * d * d;
			c = c * (0 > a.getLng() ? -1 : 1);
			d = d * (0 > a.getLat() ? -1 : 1);
			return new Point(c, d);
		}
		return null;
	}

	private    Pixel cM(Point a) {
		return pm(a);
	}

	private  Point mp(Pixel a)
	{
		double[] f = new double[0];
		Point t = new Point(Math.abs(a.getX()), Math.abs(a.getY()));
		for (int i = 0; i < mB.length; i++)
		{
			if (t.getLat() >= mB[i])
			{
				f = mL[i];
				break;
			}
		}
		return cpx(a, f);
	}


	private  Pixel pm(Point a)
	{
		double[] f = null;
		int i;
		for (i = 0; i < lB.length; i++)
		{
			if (a.getLat() >= lB[i])
			{
				f = lC[i];
				break;
			}
		}
		if (f == null)
		{
			for (i = 0; i < lB.length; i++)
			{
				if (a.getLat() <= -lB[i])
				{
					f = lC[i];
					break;
				}
			}
		}
		Pixel p=cpt(a, f);
		return  p;
	}

	private  Pixel cpt(Point a, double[] f)
	{
		double x = f[0] + f[1] * Math.abs( a.getLng());
		double temp = Math.abs(a.getLat()) / f[9];
		double y = f[2] + f[3] * temp
				+ f[4] * temp * temp
				+ f[5] * temp * temp * temp
				+ f[6] * temp * temp * temp * temp
				+ f[7] * temp * temp * temp * temp * temp
				+ f[8] * temp * temp * temp * temp * temp * temp;
		x *= (a.getLng() < 0 ? -1 : 1);
		y *= (a.getLat() < 0 ? -1 : 1);
		return new Pixel(x,y);
	}

	private  Point cpx(Pixel a, double[] f)
	{
		double x = f[0] + f[1] * Math.abs( a.getX());
		double temp = Math.abs(a.getY()) / f[9];
		double y = f[2] + f[3] * temp
				+ f[4] * temp * temp
				+ f[5] * temp * temp * temp
				+ f[6] * temp * temp * temp * temp
				+ f[7] * temp * temp * temp * temp * temp
				+ f[8] * temp * temp * temp * temp * temp * temp;
		x *= (a.getX() < 0 ? -1 : 1);
		y *= (a.getY() < 0 ? -1 : 1);
		return new Point(x,y);
	}

	private    Pixel bc(Point a,int z,Point c,Pixel d)
	{
		Point s=zb(a);
		double b = yc(z);
		Pixel p=cM(c);
		return  new Pixel(Math.round((s.getLng() - p.getX()) / b + d.getX() / 2), Math.round((p.getY() - s.getLat()) / b + d.getY() / 2));
	}

	private  Point [] cR(Point a,int b,Pixel d)
	{
		Point p1=this.ak(new Pixel(0,d.getY()),b,a,d);
		Point p2=this.ak(new Pixel(d.getX(),0),b,a,d);
		return new Point[]{p1,p2};
	}

	private  Point cT(Point a,Point b) {
		Pixel _a = this.pm(a);
		Pixel _b = this.pm(b);
		Point ab = this.mp(new Pixel((_a.getX() + _b.getX()) / 2, (_a.getY() + _b.getY()) / 2));
		ab.Round();
		return ab;
	}

	private    Point zb(Point a)
	{
		if (a==null|| 180 < a.getLng() || -180 > a.getLng() || 90 < a.getLat() || -90 > a.getLat()) {
			return new Point(0,0);
		}
		double []  c=new double[]{};
		a.setLng(OD(a.getLng(), -180, 180));
		a.setLat(SD(a.getLat(), -74, 74));
		Point	b = new Point(a.getLng(), a.getLat());
		for (int d = 0; d < lB.length; d++) {
			if (b.getLat() >= lB[d]) {
				c = qG[d];
				break;
			}
		}
		if (c.length>0) {
			for (int d = 0; d < lB.length; d++) if (b.getLat() <= -lB[d]) {
				c = qG[d];
				break;
			}
		}
		a = qK(a, c);
		a.Round();
		return  a;
	}

	private  double SD(double a,double b,double c)
	{
		if((Double)b != null){
			a = Math.max(a, b);
		}
		if((Double)c!=null)
		{
			a = Math.min(a, c);
		}
		return a;
	}
	private    double OD(double a,double b,double c)
	{
		for (; a > c;) {
			a -= c - b;
		}
		for (; a < b;) {
			a += c - b;
		}
		return a;
	}

	private boolean oc(double a, double b) {
		return !(a > 73.66 && a < 135.05 && b > 3.86 && b < 53.55);
	}

	private  double yc(int a) {
		return Math.pow(2, 18 - a);
	}

	/**
	 * Coordinate system conversion
	 * @param P spherical coordinates
	 * @param T conversion type
	 * @return Point
	 */
	public Point trans(Point P, String T) {
		if (P == null) {
			return null;
		}
		Point p = null;
		switch (T) {
		case "b2w":
			p = this.gw(this.bg(P));
			p.Round();
			return p;
		case "w2b":
			p = this.gb(this.wg(P));
			p.Round();
			return p;
		case "g2w":
			p = this.gw(P);
			p.Round();
			return p;
		case "w2g":
			p = this.wg(P);
			p.Round();
			return p;
		case "b2g":
			p = this.bg(P);
			p.Round();
			return p;
		case "g2b":
			p = this.gb(P);
			p.Round();
			return p;
		default:
			return P;
		}
	}

	/**
	 *Pixel coordinates to spherical coordinates
	 * @param P Pixel coordinate
	 * @param Z Map Zoom
	 * @param C Map Center Point
	 * @param S Map Container page size
	 * @return Point
	 */
	public  Point  px2pt(Pixel P, int Z,Point C, Pixel S) {
		if (P == null && S == null || (Integer) Z == null || Z <= 0 || Z >= 20) {
			return new Point(0, 0);
		}
		return this.ak(P, Z, C,S);
	}

	/**
	 * spherical coordinates to Pixel coordinates
	 * @param P spherical coordinate
	 * @param Z Map Zoom
	 * @param C Map Center Point
	 * @param S Map Container page size
	 * @return Point
	 */
	public  Pixel  pt2px(Point P, int Z,Point C, Pixel S) {
		if (P == null && S == null || (Integer) Z == null || Z <= 0 || Z >= 20) {
			return new Pixel(0, 0);
		}
		return this.bc(P, Z, C,S);
	}

	/**
	 * Plane coordinates are converted to spherical coordinates
	 * @param P Plane coordinates
	 * @return Point
	 */
	public  Point mc2pt(Pixel P)
	{
		if(P==null) {
			return new Point(0,0);
		}
		return  this.mp(P);
	}
	/**
	 * spherical coordinates are converted to Plane coordinates
	 * @param P spherical coordinates
	 * @return
	 */
	public  Pixel pt2mc(Point P) {
		if (P == null) {
			return new Pixel(0, 0);
		}
		return this.pm(P);
	}

	/**
	 * Map boundary
	 * @param P spherical coordinate
	 * @param Z Map Zoom
	 * @param S Container page size
	 * @return Point Array [sw,ne]
	 */
	public Point [] bound(Point P,int Z,Pixel S) {
		if (P == null ||  S == null || (Integer) Z == null || Z <= 0 || Z > 20) {
			return new Point[]{new Point(0, 0), new Point(0, 0)};
		}
		return this.cR(P,Z, S);
	}

	/**
	 * center spherical coordinate
	 * @param A spherical coordinate
	 * @param B spherical coordinate
	 * @return  Point
	 */
	public Point center(Point A,Point B)
	{
		if(A==null||B==null) {
			return  new Point(0,0);
		}
		return  this.cT(A,B);

	}

}