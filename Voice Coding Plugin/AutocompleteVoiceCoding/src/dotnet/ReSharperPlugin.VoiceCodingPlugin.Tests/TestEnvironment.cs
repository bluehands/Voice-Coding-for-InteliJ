using System.Threading;
using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.ReSharper.Feature.Services;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.TestFramework;
using JetBrains.TestFramework;
using JetBrains.TestFramework.Application.Zones;
using NUnit.Framework;

[assembly: Apartment(ApartmentState.STA)]

namespace ReSharperPlugin.VoiceCodingPlugin.Tests
{
    [ZoneDefinition]
    public class VoiceCodingPluginTestEnvironmentZone : ITestsEnvZone, IRequire<PsiFeatureTestZone>, IRequire<IVoiceCodingPluginZone> { }

    [ZoneMarker]
    public class ZoneMarker : IRequire<ICodeEditingZone>, IRequire<ILanguageCSharpZone>, IRequire<VoiceCodingPluginTestEnvironmentZone> { }

    [SetUpFixture]
    public class VoiceCodingPluginTestsAssembly : ExtensionTestEnvironmentAssembly<VoiceCodingPluginTestEnvironmentZone> { }
}
