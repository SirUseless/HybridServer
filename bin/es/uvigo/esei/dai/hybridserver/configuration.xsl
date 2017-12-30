<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:r="http://www.esei.uvigo.es/dai/hybridserver"
>
	<xsl:output method="html" indent="yes" encoding="utf-8"/>
	
	<xsl:template match="/" >
		<html>
			<head>
				<title>Configuracion</title>
			</head>
			<body>
				<div>
					<h1>Configuracion</h1>
					<div>
						<h3>Conexion</h3>
							<div>
								<div>
								<strong> Http: </strong><xsl:value-of select="r:configuration/r:connections/r:http"/>
							</div>
							<div>
								<strong> Web: </strong><xsl:value-of select="r:configuration/r:connections/r:webservice"/>
							</div>
							<div>
								<strong> Numero de Clientes: </strong><xsl:value-of select="r:configuration/r:connections/r:numClients"/>
							</div>
							</div>

					</div>

					<div>
						<h3>Base de datos</h3>
							<div>
								<div>
									<strong> Usuarios: </strong><xsl:value-of select="r:configuration/r:database/r:user"/>
								</div>
								<div>
									<strong> Contraseña: </strong><xsl:value-of select="r:configuration/r:database/r:password"/>
								</div>
								<div>
									<strong> Url: </strong><xsl:value-of select="r:configuration/r:database/r:url"/>
								</div>
							</div>

					</div>
					<xsl:apply-templates select="r:configuration/r:servers/r:server"/>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="r:server">
		<div>
			<h3><xsl:value-of select="@name"/></h3>
			<div>
				<strong> Wsdl: </strong><xsl:value-of select="@wsdl"/> 
			</div>
			<div>
				<strong> Namespace: </strong><xsl:value-of select="@namespace"/>
			</div>
			<div>
				<strong> Servicio: </strong><xsl:value-of select="@service"/>
			</div>
			<div>
				<strong> Direccion Http: </strong><xsl:value-of select="@httpAddress"/>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>